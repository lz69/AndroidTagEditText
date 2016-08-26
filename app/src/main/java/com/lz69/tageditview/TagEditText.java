package com.lz69.tageditview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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

    private String bgColor = "#22bd7a";

    final float scale = getContext().getResources().getDisplayMetrics().density;

    private List<String> tags = new ArrayList<String>();

    private float tagTextSize = getTextSize();

    public TagEditText(Context context) {
        super(context);
    }

    public TagEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TagEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        addOwnTextWatcher();
    }

    private void addOwnTextWatcher(){
        addTextChangedListener(myTextWatcher);
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

                //先生称SpannableString
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
        int length = tag.length();
        int halfCharLength = getHalfcharCount(tag);
        int hanCharLength = length - halfCharLength;
        int bitmapWidth = (int) ((tagTextSize * hanCharLength + halfCharLength * tagTextSize * 3 / 4 + tagTextSize * 2) * scale);
        int bitmapHeight = (int) ((tagTextSize * 2 + tagTextSize / 2) * scale);
        Bitmap bitmap = Bitmap.createBitmap(
                bitmapWidth,
                bitmapHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        String tagBgColor = "#22bd7a";

        paint.setColor(Color.parseColor(bgColor));

        int tagPaddingHorizontal = (int) (10 * scale);
        int tagPaddingVertical = (int) (10 * scale);
        int bgLeft = tagPaddingHorizontal;
        int bgTop = tagPaddingVertical;
        int bgRight = (int) (bitmapWidth - bgLeft);
        int bgBottom = (int)(bitmapHeight - bgTop);
        int cornerRadius = (int) (tagTextSize * scale);
        canvas.drawRoundRect(new RectF(bgLeft, bgTop, bgRight, bgBottom),
                cornerRadius,
                cornerRadius,
                paint);

        String tagTextColor = "#ffffff";

        paint.setColor(Color.parseColor(tagTextColor));
        paint.setTextSize(tagTextSize * scale);

        int tagTextPaddingHorizontal = (int) (tagTextSize * scale);
        int tagTextPaddingVertical = (int) (20 * scale);
        int xTagText = tagPaddingHorizontal + tagTextPaddingHorizontal;
        int yTagText = tagPaddingVertical + tagTextPaddingVertical;
        canvas.drawText(
                tag,
                xTagText,
                yTagText,
                paint);
        return bitmap;
    }

    private int getHalfcharCount(String str) {
        int count = 0;
        for(int i = 0; i < str.length(); i++) { //循环遍历字符串
            char c = str.charAt(i);
            if (c < 0xFF) {
                count++;
            }
        }
        return count;
    }

    @Override
    public float getTextSize() {
        return super.getTextSize();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
