package com.kimcy929.textviewtwoline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.text.PrecomputedTextCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.content.res.AppCompatResources;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.Locale;

public class TextViewTwoLine extends View {

    private static final String TAG = TextViewTwoLine.class.getSimpleName();

    private CharSequence textTitle;
    private CharSequence textDescription;

    private TextPaint titleTextPaint;
    private StaticLayout titleLayout;

    private TextPaint desTextPaint;
    private StaticLayout desLayout;
    private float yStartDes;

    private Drawable leftDrawableCompat;
    private int leftDrawableCompatId;
    private int drawableTintColor = 0;
    private int drawablePadding = 0;

    private int paragraphLeading;

    private int textTitleColor;
    private int descriptionColor;

    private int titleTextAppearId;
    private int descriptionTextAppearId;

    private Typeface titleTypeFace;
    private Typeface descriptionTypeFace;

    private boolean keepDefaultLineSpacing;

    public void setTextTitle(String textTitle) {
        this.textTitle = textTitle;
        updateContentBounds();
        requestLayout(); // recall onMeasure
    }

    public void setTextDescription(String textDescription) {
        this.textDescription = textDescription;
        updateContentBounds();
        requestLayout(); // recall onMeasure
    }

    public void setTextDescription(Spanned textDescription) {
        this.textDescription = textDescription;
        updateContentBounds();
        requestLayout(); // recall onMeasure
    }

    @SuppressWarnings("unused")
    public void setLeftDrawableCompat(@DrawableRes int leftDrawableId) {
        this.leftDrawableCompatId = leftDrawableId;
        leftDrawableCompat = AppCompatResources.getDrawable(getContext(), leftDrawableId);
        updateContentBounds();
        requestLayout(); // recall onMeasure
    }

    @SuppressWarnings("unused")
    public void setText(@Nullable String textTitle, @Nullable String textDescription) {
        this.textTitle = textTitle;
        this.textDescription = textDescription;
        updateContentBounds();
        requestLayout(); // recall onMeasure
    }
    
    public TextViewTwoLine(Context context) {
        super(context);
        initTwoTextView(context, null, 0);
    }

    // this constructor is used when created from xml
    public TextViewTwoLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTwoTextView(context, attrs, 0);
    }

    public TextViewTwoLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTwoTextView(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextViewTwoLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initTwoTextView(context, attrs, defStyleAttr);
    }

    //https://stackoverflow.com/a/46477727
    private TypedValue resolveThemeAttr(@AttrRes int attrRes) {
        Resources.Theme theme = getContext().getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attrRes, typedValue, true);
        return typedValue;
    }

    @ColorInt
    private int resolveColorAttr(@AttrRes int colorAttr) throws Resources.NotFoundException {
        TypedValue resolvedAttr = resolveThemeAttr(colorAttr);
        // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
        int colorRes = resolvedAttr.resourceId != 0 ? resolvedAttr.resourceId : resolvedAttr.data;
        return ContextCompat.getColor(getContext(), colorRes);
    }

    @SuppressWarnings("unused")
    @SuppressLint("LogNotTimber")
    private void initTwoTextView(Context context, AttributeSet attributeSet, int defStyleAttr) {

        try {
            textTitleColor = resolveColorAttr(android.R.attr.textColorPrimary);
        } catch (Resources.NotFoundException e) {
            textTitleColor = 0xDE000000;
            Log.e(TAG, "Error get textColorPrimary " + e.getMessage());
        }

        try {
            descriptionColor = resolveColorAttr(android.R.attr.textColorSecondary);
        } catch (Resources.NotFoundException e) {
            descriptionColor = 0x8A000000;
            Log.e(TAG, "Error get textColorSecondary " + e.getMessage());
        }

        int titleTextSize = spToPx(14f);
        int descriptionTextSize = spToPx(14f);

        int titleTextStyle;
        int descriptionTextStyle;

        if (attributeSet != null) {
            TypedArray a = null;
            try {
                a = context.obtainStyledAttributes(attributeSet, R.styleable.TextViewTwoLine);

                textTitle = a.getString(R.styleable.TextViewTwoLine_textTitle);
                textDescription = a.getString(R.styleable.TextViewTwoLine_textDescription);
                drawablePadding = a.getDimensionPixelSize(R.styleable.TextViewTwoLine_drawablePadding, 0);
                drawableTintColor = a.getColor(R.styleable.TextViewTwoLine_drawableTintColor, 0);

                leftDrawableCompatId = a.getResourceId(R.styleable.TextViewTwoLine_leftDrawableCompat, 0);
                if (leftDrawableCompatId != 0) {
                    leftDrawableCompat = AppCompatResources.getDrawable(context, leftDrawableCompatId);
                }

                int fontId = a.getResourceId(R.styleable.TextViewTwoLine_titleFontFamily, 0);
                if (fontId != 0) {
                    try {
                        titleTypeFace = ResourcesCompat.getFont(getContext(), fontId);
                    } catch (Resources.NotFoundException e) {
                        Log.e(TAG, "Not found title fontFamily");
                    }
                }

                titleTextStyle = a.getInt(R.styleable.TextViewTwoLine_TextViewTwoLineTitleTextStyle, 0); //regular
                if (titleTypeFace != null) {
                    if (titleTextStyle != 0) {
                        titleTypeFace = Typeface.create(titleTypeFace, titleTextStyle);
                    }
                } else if (titleTextStyle != 0) {
                    titleTypeFace = Typeface.defaultFromStyle(titleTextStyle);
                }

                fontId = a.getResourceId(R.styleable.TextViewTwoLine_descriptionFontFamily, 0);
                if (fontId != 0) {
                    try {
                        descriptionTypeFace = ResourcesCompat.getFont(getContext(), fontId);
                    } catch (Resources.NotFoundException e) {
                        Log.e(TAG, "Not found description fontFamily");
                    }
                }

                descriptionTextStyle = a.getInt(R.styleable.TextViewTwoLine_TextViewTwoLineDescriptionTextStyle, 0); //regular
                if (descriptionTypeFace != null) {
                    if (descriptionTextStyle != 0) {
                        descriptionTypeFace = Typeface.create(descriptionTypeFace, descriptionTextStyle);
                    }
                } else if (descriptionTextStyle != 0) {
                    descriptionTypeFace = Typeface.defaultFromStyle(descriptionTextStyle);
                }

                titleTextAppearId = a.getResourceId(R.styleable.TextViewTwoLine_titleTextAppearance, 0);
                descriptionTextAppearId = a.getResourceId(R.styleable.TextViewTwoLine_descriptionTextAppearance, 0);

                int defaultTextSize = spToPx(14f);
                titleTextSize = a.getDimensionPixelSize(R.styleable.TextViewTwoLine_titleTextSize, defaultTextSize);
                descriptionTextSize = a.getDimensionPixelSize(R.styleable.TextViewTwoLine_descriptionTextSize, defaultTextSize);

                textTitleColor = a.getColor(R.styleable.TextViewTwoLine_textTitleColor, textTitleColor);
                descriptionColor = a.getColor(R.styleable.TextViewTwoLine_textDescriptionColor, descriptionColor);

                keepDefaultLineSpacing = a.getBoolean(R.styleable.TextViewTwoLine_keepDefaultLineSpacing, false);
            } finally {
                if (a != null) {
                    a.recycle();
                }
            }
        }

        if (titleTextAppearId != 0) {
            titleTextSize = getTextSizeFromTextAppearance(titleTextAppearId, titleTextSize);
        }

        if (descriptionTextAppearId != 0) {
            descriptionTextSize = getTextSizeFromTextAppearance(descriptionTextAppearId, descriptionTextSize);
        }

        // init for title
        titleTextPaint = new TextPaint();
        titleTextPaint.setAntiAlias(true);
        titleTextPaint.setTextSize(titleTextSize);
        titleTextPaint.setTextLocale(Locale.getDefault());
        if (titleTypeFace != null) {
            titleTextPaint.setTypeface(titleTypeFace);
        }

        // default to a single line of text
        if (!TextUtils.isEmpty(textTitle)) {
            int width = (int) titleTextPaint.measureText(textTitle.toString());
            createTitleLayout(width);
        }
        
        // init for description
        desTextPaint = new TextPaint();
        desTextPaint.setAntiAlias(true);
        desTextPaint.setTextSize(descriptionTextSize);
        desTextPaint.setTextLocale(Locale.getDefault());
        if (descriptionTypeFace != null) {
            desTextPaint.setTypeface(descriptionTypeFace);
        }

        // default to a single line of text
        if (!TextUtils.isEmpty(textDescription)) {
            int width = (int) desTextPaint.measureText(textDescription.toString());
            createDescriptionLayout(width);
        }
    }

    private int getTextSizeFromTextAppearance(int textAppearanceId, int defaultTextSize) {
        TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan(getContext(), textAppearanceId);
        int textSize = textAppearanceSpan.getTextSize();
        if (textSize != -1) {
            return textSize;
        }
        return defaultTextSize;
    }

    @NonNull
    private Layout.Alignment getAlignment() {
        return Layout.Alignment.ALIGN_NORMAL;
    }

    private void createTitleLayout(int width) {
        SpannableStringBuilder stringBuilder;

        if (titleTextAppearId != 0) {
            stringBuilder = getSpannableStringBuilder(textTitle.toString(), titleTextAppearId);
        } else {
            stringBuilder = new SpannableStringBuilder(textTitle);
        }

        if (textTitleColor != 0) {
            stringBuilder.setSpan(new ForegroundColorSpan(textTitleColor), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (titleTypeFace != null) {
            stringBuilder.setSpan(new CustomTypefaceSpan(titleTypeFace), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TextDirectionHeuristic textDirection = TextDirectionHeuristics.LTR;
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                textDirection = TextDirectionHeuristics.RTL;
            }

            titleLayout = StaticLayout.Builder.obtain(stringBuilder, 0, stringBuilder.length(), titleTextPaint, width)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setTextDirection(textDirection)
                    .setLineSpacing(0, 1.0f) // multiplier, add
                    .setBreakStrategy(Layout.BREAK_STRATEGY_HIGH_QUALITY)
                    .setIncludePad(false)
                    .build();
        } else {
            titleLayout = new StaticLayout(stringBuilder, titleTextPaint, width, getAlignment(), 1.0f, 0, false);
        }
    }

    private void createDescriptionLayout(int width) {
        SpannableStringBuilder stringBuilder;

        if (descriptionTextAppearId != 0) {
            stringBuilder = getSpannableStringBuilder(textDescription.toString(), descriptionTextAppearId);
        } else {
            stringBuilder = new SpannableStringBuilder(textDescription);
        }

        if (descriptionColor != 0) {
            stringBuilder.setSpan(new ForegroundColorSpan(descriptionColor), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (descriptionTypeFace != null) {
            stringBuilder.setSpan(new CustomTypefaceSpan(descriptionTypeFace), 0, stringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TextDirectionHeuristic textDirection = TextDirectionHeuristics.LTR;
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                textDirection = TextDirectionHeuristics.RTL;
            }

            desLayout = StaticLayout.Builder.obtain(stringBuilder, 0, stringBuilder.length(), desTextPaint, width)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setTextDirection(textDirection)
                    .setLineSpacing(0, 1.0f) // multiplier,
                    .setBreakStrategy(Layout.BREAK_STRATEGY_HIGH_QUALITY)
                    .setIncludePad(false)
                    .build();

        } else {
            desLayout = new StaticLayout(stringBuilder, desTextPaint, width, getAlignment(), 1.0f, 0, false);
        }
    }

    @NonNull
    private SpannableStringBuilder getSpannableStringBuilder(@NonNull String text, @StyleRes int textAppearStyle) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
        stringBuilder.setSpan(new TextAppearanceSpan(getContext(), textAppearStyle), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return stringBuilder;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            updateContentBounds();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = View.resolveSize(getDesireWidth(widthMeasureSpec), widthMeasureSpec);

        int height = View.resolveSize(getDesireHeight(heightMeasureSpec), heightMeasureSpec);

        setMeasuredDimension(width, height);

        updateContentBounds();
    }

    private int getDesireWidth(int widthMeasureSpec) {

        int desireWidth = 0;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthRequirement = MeasureSpec.getSize(widthMeasureSpec);

        desireWidth += getMinimumWidth();

        if (widthMode == MeasureSpec.EXACTLY) {
            desireWidth = widthRequirement;
        } else {
            int drawableSize = 0;
            if (leftDrawableCompat != null) {
                drawableSize = leftDrawableCompat.getIntrinsicWidth();
            }

            int keyLine = getPaddingStart() + drawableSize + drawablePadding;

            desireWidth += keyLine;

            if (titleLayout != null && desLayout != null) {
                desireWidth += Math.max(titleLayout.getWidth(), desLayout.getWidth());
            } else if (titleLayout != null) {
                desireWidth += titleLayout.getWidth();
            } else if (desLayout != null) {
                desireWidth += desLayout.getWidth();
            } else {
                if (leftDrawableCompat != null) {
                    desireWidth = leftDrawableCompat.getIntrinsicWidth();
                }
            }

            desireWidth += getPaddingEnd();

            if (widthMode == MeasureSpec.AT_MOST) {
                if (desireWidth > widthRequirement) {
                    desireWidth = widthRequirement;
                }
            } else if (widthMode == MeasureSpec.UNSPECIFIED) {
                desireWidth = getDefaultSize(desireWidth, widthMeasureSpec);
            }
        }

        return desireWidth;
    }

    private int getDesireHeight(int heightMeasureSpec) {
        int desireHeight = 0;

        desireHeight += getPaddingTop() + getPaddingBottom();

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightRequirement = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            desireHeight = heightRequirement;
        } else {
            int descriptionTextHeight;
            int totalTextHeight = 0;

            if (titleLayout != null) {
                totalTextHeight += titleLayout.getHeight();
            }

            if (desLayout != null) {
                descriptionTextHeight = desLayout.getHeight();
                if (titleLayout != null) {
                    paragraphLeading = (int) (titleTextPaint.getFontMetrics().bottom * (keepDefaultLineSpacing ? 1.0f : 1.2f));
                    totalTextHeight += descriptionTextHeight + paragraphLeading;
                } else {
                    totalTextHeight += descriptionTextHeight;
                }
            }

            if (leftDrawableCompat != null) {
                desireHeight += Math.max(leftDrawableCompat.getIntrinsicHeight(), totalTextHeight);
            } else {
                desireHeight = totalTextHeight;
            }

            if (heightMode == MeasureSpec.AT_MOST) {
                desireHeight = Math.min(desireHeight, heightRequirement);
            } else if (heightMode == MeasureSpec.UNSPECIFIED) { // for scrollview
                desireHeight = Math.max(desireHeight, getMinimumHeight());
                desireHeight = getDefaultSize(desireHeight, heightMeasureSpec);
            }
        }

        return desireHeight;
    }

    private int xStartTitle, yStartTitle;

    private void updateContentBounds() {

        int drawableSize = 0;
        int left = getPaddingStart();

        if (leftDrawableCompat != null) {
            drawableSize = leftDrawableCompat.getIntrinsicWidth();
            if (drawableTintColor != 0) {
                leftDrawableCompat.mutate().setColorFilter(drawableTintColor, PorterDuff.Mode.SRC_IN);
            } else {
                leftDrawableCompat.mutate().setColorFilter(null);
            }
        }

        int textWidth = getMeasuredWidth() - getPaddingStart()
                - drawableSize - drawablePadding - getPaddingEnd(); // keyLine = paddingStart + drawableSize + drawablePadding = 16dp + 24dp + 32dp even view hasn't a drawable

        if (!TextUtils.isEmpty(textTitle) && textWidth >  0) {
            createTitleLayout(textWidth);
        } else {
            titleLayout = null;
        }

        if (!TextUtils.isEmpty(textDescription) && textWidth >  0) {
            createDescriptionLayout(textWidth);
        } else {
            desLayout = null;
        }

        int titleLayoutWidth = 0;
        int titleLayoutHeight = 0;

        if (titleLayout != null) {
            titleLayoutHeight = titleLayout.getHeight();
            titleLayoutWidth = titleLayout.getWidth();
        } else if (desLayout != null) {
            titleLayoutWidth = desLayout.getWidth();
        }

        if (titleLayout != null) {
            if (desLayout != null) {
                yStartTitle = (getMeasuredHeight() - desLayout.getHeight() - paragraphLeading - titleLayoutHeight) / 2;
            } else {
                yStartTitle = (getMeasuredHeight() - titleLayoutHeight) / 2;
            }
        }

        if (desLayout != null) {
            if (titleLayout != null) {
                yStartDes = yStartTitle + titleLayoutHeight + paragraphLeading;
            } else {
                yStartDes = (getMeasuredHeight() - desLayout.getHeight()) / 2;
            }
        }

        // default is LTR
        xStartTitle = left + drawableSize + drawablePadding; //left + drawableSize + drawablePadding; //72dp
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        if (layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL) {
            left = getMeasuredWidth() - drawableSize - left;
            xStartTitle = getMeasuredWidth() - xStartTitle - titleLayoutWidth;
        }

        if (leftDrawableCompat != null) {
            int top = (getMeasuredHeight() - drawableSize) / 2;
            leftDrawableCompat.setBounds(left, top, left + drawableSize, top + drawableSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // do as little as possible inside onDraw to improve performance

        if (leftDrawableCompat != null) {
            leftDrawableCompat.draw(canvas);
        }

        if (titleLayout != null) {
            canvas.save();
            canvas.translate(xStartTitle, yStartTitle);
            titleLayout.draw(canvas);
            canvas.restore();
        }

        if (desLayout != null) {
            canvas.save();
            canvas.translate(xStartTitle, yStartDes);
            desLayout.draw(canvas);
            canvas.restore();
        }
    }

    // https://stackoverflow.com/questions/3542333/how-to-prevent-custom-views-from-losing-state-across-screen-orientation-changes
    // https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/java/android/widget/TextView.java#L5001

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superSate = super.onSaveInstanceState();

        if (!TextUtils.isEmpty(textTitle) || !TextUtils.isEmpty(textDescription) || leftDrawableCompatId != 0) {

            SavedState ss = new SavedState(superSate);

            if (!TextUtils.isEmpty(textTitle)) {
                ss.textTitle = textTitle;
            }

            if (!TextUtils.isEmpty(textDescription)) {
                ss.textDescription = textDescription;
            }

            if (leftDrawableCompatId != 0) {
                ss.leftDrawableId = leftDrawableCompatId;
            }

            return ss;
        }

        return superSate;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());

        if (!TextUtils.isEmpty(ss.textTitle)) {
            textTitle = ss.textTitle;
        }

        if (!TextUtils.isEmpty(ss.textDescription)) {
            textDescription = ss.textDescription;
        }

        if (ss.leftDrawableId != 0) {
            leftDrawableCompatId = ss.leftDrawableId;
            leftDrawableCompat = AppCompatResources.getDrawable(getContext(), leftDrawableCompatId);
        }
    }

    public static class SavedState extends BaseSavedState {

        CharSequence textTitle = null;
        CharSequence textDescription = null;
        int leftDrawableId = 0;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            TextUtils.writeToParcel(textTitle, out, flags);

            TextUtils.writeToParcel(textDescription, out, flags);

            out.writeInt(leftDrawableId);
        }

        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

        private SavedState(Parcel in) {
            super(in);

            textTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);

            textDescription = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);

            leftDrawableId = in.readInt();
        }
    }

    public int spToPx(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getContext().getResources().getDisplayMetrics());
    }
}
