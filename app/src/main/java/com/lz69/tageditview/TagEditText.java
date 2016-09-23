package com.lz69.tageditview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class TagEditText extends EditText{

    private int bgColor;

    private int DEFAULT_BG_COLOR = Color.parseColor("#22BD7A");

    private int tagTextColor;

    private int DEFAULT_TAG_TEXT_COLOR = Color.parseColor("#FFFFFF");

    private int SHADOW_DEEP = 3;

    protected final float SCALE = getContext().getResources().getDisplayMetrics().density;

    private List<String> tags = new ArrayList<String>();

    private float tagTextSize = getTextSize();

    public TagEditText(Context context) {
        super(context);
        init(null, 0);
    }

    public TagEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TagEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        final TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.TagEditText, defStyleAttr, 0);

        initAttributes(attrArray);

        attrArray.recycle();

        addOwnTextWatcher();
    }

    private void addOwnTextWatcher(){
        addTextChangedListener(myTextWatcher);
    }

    protected void initAttributes(TypedArray attrArray) {
        bgColor = attrArray.getColor(R.styleable.TagEditText_bgColor, DEFAULT_BG_COLOR);
        tagTextColor = attrArray.getColor(R.styleable.TagEditText_tagTextColor, DEFAULT_TAG_TEXT_COLOR);
    }

    private TextWatcher myTextWatcher = new TextWatcher() {

        private String beforeText = "";

        private SpannableString spannableString = new SpannableString("");

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.i("lz69", "beforeTextChanged:" + "charSequence:" + charSequence + "i:" + i + "i1:" + i1 + "i2" + i2);
            beforeText = charSequence.toString();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.i("lz69", "onTextChanged:" + "charSequence:" + charSequence + "i:" + i + "i1:" + i1 + "i2" + i2);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            Log.i("lz69", "afterTextChanged:" + editable);
            if (tags != null && !tags.isEmpty() && beforeText.length() == editable.length() + tags.get(tags.size() - 1).length()) {
                tags.remove(tags.size()-1);
                Log.i("lz69", "tags:" + tags);
            }
            String afterString = editable.toString();
            //修改为图片后还会地用一次，中和矛盾
            String newString = null;
            if(afterString.length() >= beforeText.length()) {
                newString = afterString.substring(getTagsLength(), beforeText.length());
            } else {
                newString = afterString;
            }
            if (afterString != null && afterString.length() != 0
                    && (afterString.charAt(afterString.length() - 1) == ' ')
                    && !afterString.equals(spannableString.toString())) {
                //去除空格
                String newTag = newString;

                tags.add(newTag);

                //先生成SpannableString
                String allTag = "";
                for (String tag:tags) {
                    allTag += tag;
                }
                spannableString = new SpannableString(allTag);

                //替换tag为图片
                int start = 0;
                for (String tag:tags) {
                    int end = start + tag.length();
                    ImageSpan imageSpan = new ImageSpan(getTextBitmap(tag));
                    spannableString.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    start = end;
                }

                setText(spannableString);

                setSelection(getText().length());
            }
        }
    };

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        setSelection(getText().length());
    }

    private int getTagsLength() {
        int length = 0;
        for (String tag:tags) {
            length += tag.length();
        }
        return length;
    }

    private Bitmap getTextBitmap(String tag) {

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(tagTextSize * SCALE);
        textPaint.setColor(tagTextColor);
        textPaint.setSubpixelText(true);
        Typeface tf = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);

        textPaint.setTypeface(tf);

        Rect textRect = new Rect();
        textPaint.getTextBounds(tag, 0, tag.length(), textRect);
        int textBoundLeft = textRect.left;
        int textBoundRight = textRect.right;
        int textBoundTop = textRect.top;
        int textBoundBottom = textRect.bottom;

        int cornerRadius = (int) (tagTextSize * SCALE);
        int tagPaddingHorizontal = (int) (10 * SCALE);

        int tagPaddingVertical = (int) (10 * SCALE);

        int tagTextPaddingHorizontal = (int) (tagTextSize * SCALE);

        int tagTextPaddingVertical = (int) (tagTextSize / 2 * SCALE);

        int bitmapWidth = (tagPaddingHorizontal + tagTextPaddingHorizontal) * 2 + (textBoundRight - textBoundLeft);

        int bitmapHeight = (textBoundBottom - textBoundTop) + tagPaddingVertical * 2 + tagTextPaddingVertical * 2;

        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(bgColor);
        int bgLeft = tagPaddingHorizontal;
        int bgTop = tagPaddingVertical;
        int bgRight = bitmapWidth - bgLeft;
        int bgBottom = bitmapHeight - bgTop;
        bgPaint.setShadowLayer(30, SHADOW_DEEP * SCALE, SHADOW_DEEP * SCALE, Color.parseColor("#BBBBBB"));
        canvas.drawRoundRect(new RectF(bgLeft, bgTop, bgRight, bgBottom), cornerRadius, cornerRadius, bgPaint);

        int xTagText = -textBoundLeft + tagPaddingHorizontal + tagTextPaddingHorizontal;
        int yTagText = -textBoundTop + tagPaddingVertical + tagTextPaddingVertical;
        canvas.drawText(tag, xTagText, yTagText, textPaint);
        return bitmap;
    }

}
