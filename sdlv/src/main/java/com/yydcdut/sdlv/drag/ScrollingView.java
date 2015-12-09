package com.yydcdut.sdlv.drag;


/**
 * An interface that can be implemented by Views to provide scroll related APIs.
 */
public interface ScrollingView {
    /**
     * <p>Compute the horizontal range that the horizontal scrollbar
     * represents.</p>
     * <p>
     * <p>The range is expressed in arbitrary units that must be the same as the
     * units used by {@link #computeHorizontalScrollExtent()} and
     * {@link #computeHorizontalScrollOffset()}.</p>
     * <p>
     * <p>The default range is the drawing width of this view.</p>
     *
     * @return the total horizontal range represented by the horizontal
     * scrollbar
     * @see #computeHorizontalScrollExtent()
     * @see #computeHorizontalScrollOffset()
     * @see //android.widget.ScrollBarDrawable
     */
    int computeHorizontalScrollRange();

    /**
     * <p>Compute the horizontal offset of the horizontal scrollbar's thumb
     * within the horizontal range. This value is used to compute the position
     * of the thumb within the scrollbar's track.</p>
     * <p>
     * <p>The range is expressed in arbitrary units that must be the same as the
     * units used by {@link #computeHorizontalScrollRange()} and
     * {@link #computeHorizontalScrollExtent()}.</p>
     * <p>
     * <p>The default offset is the scroll offset of this view.</p>
     *
     * @return the horizontal offset of the scrollbar's thumb
     * @see #computeHorizontalScrollRange()
     * @see #computeHorizontalScrollExtent()
     * @see //android.widget.ScrollBarDrawable
     */
    int computeHorizontalScrollOffset();

    /**
     * <p>Compute the horizontal extent of the horizontal scrollbar's thumb
     * within the horizontal range. This value is used to compute the length
     * of the thumb within the scrollbar's track.</p>
     * <p>
     * <p>The range is expressed in arbitrary units that must be the same as the
     * units used by {@link #computeHorizontalScrollRange()} and
     * {@link #computeHorizontalScrollOffset()}.</p>
     * <p>
     * <p>The default extent is the drawing width of this view.</p>
     *
     * @return the horizontal extent of the scrollbar's thumb
     * @see #computeHorizontalScrollRange()
     * @see #computeHorizontalScrollOffset()
     * @see //android.widget.ScrollBarDrawable
     */
    int computeHorizontalScrollExtent();

    /**
     * <p>Compute the vertical range that the vertical scrollbar represents.</p>
     * <p>
     * <p>The range is expressed in arbitrary units that must be the same as the
     * units used by {@link #computeVerticalScrollExtent()} and
     * {@link #computeVerticalScrollOffset()}.</p>
     *
     * @return the total vertical range represented by the vertical scrollbar
     * <p>
     * <p>The default range is the drawing height of this view.</p>
     * @see #computeVerticalScrollExtent()
     * @see #computeVerticalScrollOffset()
     * @see //android.widget.ScrollBarDrawable
     */
    int computeVerticalScrollRange();

    /**
     * <p>Compute the vertical offset of the vertical scrollbar's thumb
     * within the horizontal range. This value is used to compute the position
     * of the thumb within the scrollbar's track.</p>
     * <p>
     * <p>The range is expressed in arbitrary units that must be the same as the
     * units used by {@link #computeVerticalScrollRange()} and
     * {@link #computeVerticalScrollExtent()}.</p>
     * <p>
     * <p>The default offset is the scroll offset of this view.</p>
     *
     * @return the vertical offset of the scrollbar's thumb
     * @see #computeVerticalScrollRange()
     * @see #computeVerticalScrollExtent()
     * @see //android.widget.ScrollBarDrawable
     */
    int computeVerticalScrollOffset();

    /**
     * <p>Compute the vertical extent of the vertical scrollbar's thumb
     * within the vertical range. This value is used to compute the length
     * of the thumb within the scrollbar's track.</p>
     * <p>
     * <p>The range is expressed in arbitrary units that must be the same as the
     * units used by {@link #computeVerticalScrollRange()} and
     * {@link #computeVerticalScrollOffset()}.</p>
     * <p>
     * <p>The default extent is the drawing height of this view.</p>
     *
     * @return the vertical extent of the scrollbar's thumb
     * @see #computeVerticalScrollRange()
     * @see #computeVerticalScrollOffset()
     * @see //android.widget.ScrollBarDrawable
     */
    int computeVerticalScrollExtent();
}
