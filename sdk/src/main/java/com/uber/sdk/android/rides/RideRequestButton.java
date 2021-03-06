/*
 * Copyright (c) 2015 Uber Technologies, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.uber.sdk.android.rides;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * An Uber styled button to request rides with specific {@link RideParameters}. Default {@link RideParameters} is
 * set to a pickup of the device's location. Requires a client ID to function.
 */
public class RideRequestButton extends UberButton {

    private static final String USER_AGENT_BUTTON = "rides-android-v0.3.1-button";

    @NonNull private RideRequestBehavior mRequestBehavior = new RequestDeeplinkBehavior();
    @NonNull private RideParameters mRideParameters = new RideParameters.Builder().build();

    public RideRequestButton(Context context) {
        this(context, null);
    }

    public RideRequestButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.uberButtonStyle);
    }

    public RideRequestButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
    }

    /**
     * Sets the {@link RideParameters} that will be used to request a ride when the button is clicked. If null will
     * use default RideParameters behavior.
     */
    public void setRideParameters(@Nullable RideParameters rideParameters) {
        if (rideParameters == null) {
            rideParameters = new RideParameters.Builder().build();
        }
        mRideParameters = rideParameters;
    }

    /**
     * Sets how the request button should act for button actions.
     *
     * @param requestBehavior an object that implements {@link RideRequestBehavior}
     */
    public void setRequestBehavior(@NonNull RideRequestBehavior requestBehavior) {
        mRequestBehavior = requestBehavior;
    }

    @Override
    protected void init(
            @NonNull Context context,
            @Nullable AttributeSet attributeSet,
            int defStyleAttrs,
            int defStyleRes) {
        Style style = Style.DEFAULT;
        if (attributeSet != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributeSet,
                    R.styleable.RideRequestButton, 0, 0);
            style = Style.fromInt(typedArray.getInt(R.styleable.RideRequestButton_ub__style,
                    Style.DEFAULT.getValue()));
            typedArray.recycle();
        }
        // If no style specified, or just the default UberButton style, use the style attribute
        defStyleRes = defStyleRes == 0 || defStyleRes == R.style.UberButton ? style.getStyleId() : defStyleRes;

        super.init(context, attributeSet, defStyleAttrs, defStyleRes);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mRideParameters.setUserAgent(USER_AGENT_BUTTON);
                mRequestBehavior.requestRide(getContext(), mRideParameters);
            }
        });
    }

    /**
     * Encapsulates the valid values for the uber:color_scheme attribute for a {@link RideRequestButton}
     */
    private enum Style {
        /**
         * Black background, white text. This is the default.
         */
        BLACK(0, R.style.UberButton_RideRequest),

        /**
         * White background, black text.
         */
        WHITE(1, R.style.UberButton_RideRequest_White);

        private static Style DEFAULT = BLACK;

        private int mIntValue;
        private int mStyleId;

        Style(int value, int styleId) {
            this.mIntValue = value;
            this.mStyleId = styleId;
        }

        /**
         * If the value is not found returns default Style.
         */
        @NonNull
        static Style fromInt(int enumValue) {
            for (Style style : values()) {
                if (style.getValue() == enumValue) {
                    return style;
                }
            }

            return DEFAULT;
        }

        private int getValue() {
            return mIntValue;
        }

        private int getStyleId() {
            return mStyleId;
        }
    }
}
