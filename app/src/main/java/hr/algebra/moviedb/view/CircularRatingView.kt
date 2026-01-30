package hr.algebra.moviedb.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import hr.algebra.moviedb.R
import kotlin.math.min

class CircularRatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Paint objects for drawing
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Drawing bounds
    private val rectF = RectF()

    // Configuration properties
    private var rating: Float = 0f
    private var animatedRating: Float = 0f
    private var maxRating: Float = 10f
    private var strokeWidth: Float = 8f
    private var backgroundCircleColor: Int = Color.LTGRAY
    private var showRatingText: Boolean = true
    private var ratingTextSize: Float = 24f
    private var ratingTextColor: Int = Color.WHITE
    private var useDynamicColors: Boolean = true

    // Animation
    private var animator: ValueAnimator? = null

    // Dynamic colors for rating ranges
    companion object {
        private const val COLOR_LOW = 0xFFE53935.toInt()      // Red - rating 0-4
        private const val COLOR_MEDIUM = 0xFFFFB300.toInt()   // Amber - rating 4-7
        private const val COLOR_HIGH = 0xFF43A047.toInt()     // Green - rating 7-10
        
        private const val ANIMATION_DURATION = 800L
        private const val START_ANGLE = -90f  // Start from top
    }

    init {
        // Load custom attributes
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CircularRatingView,
            0, 0
        ).apply {
            try {
                rating = getFloat(R.styleable.CircularRatingView_rating, 0f)
                maxRating = getFloat(R.styleable.CircularRatingView_maxRating, 10f)
                strokeWidth = getDimension(R.styleable.CircularRatingView_strokeWidth, 8f)
                backgroundCircleColor = getColor(
                    R.styleable.CircularRatingView_backgroundCircleColor,
                    Color.parseColor("#40FFFFFF")
                )
                showRatingText = getBoolean(R.styleable.CircularRatingView_showRatingText, true)
                ratingTextSize = getDimension(R.styleable.CircularRatingView_ratingTextSize, 24f)
                ratingTextColor = getColor(R.styleable.CircularRatingView_ratingTextColor, Color.WHITE)
                useDynamicColors = getBoolean(R.styleable.CircularRatingView_useDynamicColors, true)
            } finally {
                recycle()
            }
        }

        // Initialize the animated rating to match the initial rating
        animatedRating = rating

        setupPaints()
    }

    private fun setupPaints() {
        // Background circle paint
        backgroundPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = this@CircularRatingView.strokeWidth
            color = backgroundCircleColor
            strokeCap = Paint.Cap.ROUND
        }

        // Progress arc paint
        progressPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = this@CircularRatingView.strokeWidth
            color = getColorForRating(rating)
            strokeCap = Paint.Cap.ROUND
        }

        // Center text paint
        textPaint.apply {
            color = ratingTextColor
            textSize = ratingTextSize
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
    }

    /**
     * Returns the appropriate color based on the rating value.
     * - Red (0-4): Low rating
     * - Amber (4-7): Medium rating
     * - Green (7-10): High rating
     */
    private fun getColorForRating(ratingValue: Float): Int {
        if (!useDynamicColors) {
            return ContextCompat.getColor(context, R.color.teal_200)
        }
        
        return when {
            ratingValue < 4f -> COLOR_LOW
            ratingValue < 7f -> COLOR_MEDIUM
            else -> COLOR_HIGH
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = (48 * resources.displayMetrics.density).toInt()
        
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredSize, widthSize)
            else -> desiredSize
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredSize, heightSize)
            else -> desiredSize
        }

        // Ensure square dimensions
        val size = min(width, height)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        // Calculate the drawing rectangle with padding for stroke width
        val padding = strokeWidth / 2 + paddingStart + paddingEnd
        rectF.set(
            padding,
            padding,
            w - padding,
            h - padding
        )
        
        // Adjust text size based on view size
        if (ratingTextSize == 24f) {
            textPaint.textSize = (w * 0.3f)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw background circle
        canvas.drawArc(rectF, 0f, 360f, false, backgroundPaint)

        // Draw progress arc
        val sweepAngle = (animatedRating / maxRating) * 360f
        progressPaint.color = getColorForRating(animatedRating)
        canvas.drawArc(rectF, START_ANGLE, sweepAngle, false, progressPaint)

        // Draw rating text in center
        if (showRatingText) {
            val displayRating = String.format("%.1f", animatedRating)
            val textY = (height / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
            canvas.drawText(displayRating, width / 2f, textY, textPaint)
        }
    }

    fun setRating(newRating: Float, animate: Boolean = true) {
        val clampedRating = newRating.coerceIn(0f, maxRating)
        
        if (animate && isAttachedToWindow) {
            animateRating(rating, clampedRating)
        } else {
            rating = clampedRating
            animatedRating = clampedRating
            invalidate()
        }
        
        rating = clampedRating
    }

    /**
     * Gets the current rating value.
     */
    fun getRating(): Float = rating

    /**
     * Animates the rating change with a smooth transition.
     */
    private fun animateRating(from: Float, to: Float) {
        animator?.cancel()
        
        animator = ValueAnimator.ofFloat(from, to).apply {
            duration = ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                animatedRating = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    /**
     * Sets whether to use dynamic colors based on rating.
     */
    fun setUseDynamicColors(useDynamic: Boolean) {
        useDynamicColors = useDynamic
        invalidate()
    }

    /**
     * Sets the stroke width for the circular progress.
     */
    fun setStrokeWidth(width: Float) {
        strokeWidth = width
        backgroundPaint.strokeWidth = width
        progressPaint.strokeWidth = width
        requestLayout()
        invalidate()
    }

    /**
     * Sets the text color for the rating number.
     */
    fun setRatingTextColor(color: Int) {
        ratingTextColor = color
        textPaint.color = color
        invalidate()
    }

    /**
     * Sets whether to show the rating text in the center.
     */
    fun setShowRatingText(show: Boolean) {
        showRatingText = show
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}
