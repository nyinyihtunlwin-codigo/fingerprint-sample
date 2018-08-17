package com.nyinyihtunlwin.projects.fingerprintsample;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;


public class FingerprintController extends FingerprintManagerCompat.AuthenticationCallback {


    private static final long ERROR_TIMEOUT_MILLIS = 1600L;
    private static final long SUCCESS_DELAY_MILLIS = 1300L;

    private FingerprintManagerCompat mFingerprintManagerCompat;
    private Callback mCallback;

    private TextView tvTitle, tvSubtitle, tvError;
    private ImageView ivIcon;

    private CancellationSignal cancellationSignal = null;

    private boolean selfCancelled = false;

    private boolean isFingerprintAuthAvailable = false;

    private Runnable resetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            tvError.setTextColor(ContextCompat.getColor(getContext(), R.color.hint_color));
            tvError.setText(getContext().getString(R.string.touch_id_for_kbz_mbanking));
            ivIcon.setImageResource(R.drawable.ic_fingerprint_white_24dp);
        }
    };

    public FingerprintController(FingerprintManagerCompat fingerprintManagerCompat,
                                 Callback callback,
                                 TextView tvTitle,
                                 TextView tvSubtitle,
                                 TextView tvError,
                                 ImageView ivIcon) {
        this.mFingerprintManagerCompat = fingerprintManagerCompat;
        this.mCallback = callback;
        this.tvTitle = tvTitle;
        this.tvSubtitle = tvSubtitle;
        this.tvError = tvError;
        this.ivIcon = ivIcon;
        this.tvError.post(resetErrorTextRunnable);
    }

    public Boolean getFingerprintAuthAvailable() {
        return mFingerprintManagerCompat.isHardwareDetected() && mFingerprintManagerCompat.hasEnrolledFingerprints();
    }

    public Context getContext() {
        return tvError.getContext();
    }

    public CancellationSignal startListening(FingerprintManagerCompat.CryptoObject cryptoObject) {
        if (!isFingerprintAuthAvailable) {
            cancellationSignal = new CancellationSignal();
            selfCancelled = false;
            mFingerprintManagerCompat.authenticate(cryptoObject, 0, cancellationSignal, this, null);
            return cancellationSignal;
        }
        return null;
    }

    public void stopListening() {
        if (cancellationSignal != null) {
            selfCancelled = true;
            cancellationSignal.cancel();
            cancellationSignal = null;
        }
    }

    private void showError(CharSequence text) {
        ivIcon.setImageResource(R.drawable.ic_error_white_24dp);
        tvError.setText(text);
        tvError.setTextColor(ContextCompat.getColor(tvError.getContext(), R.color.warning_color));
        tvError.removeCallbacks(resetErrorTextRunnable);
        tvError.postDelayed(resetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!selfCancelled) {
            showError(errString);
            ivIcon.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCallback.onError();
                }
            }, ERROR_TIMEOUT_MILLIS);
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        tvError.removeCallbacks(resetErrorTextRunnable);
        ivIcon.setImageResource(R.drawable.ic_fingerprint_acent_24dp);
        tvError.setTextColor(ContextCompat.getColor(tvError.getContext(), R.color.success_color));
        tvError.setText(tvError.getContext().getString(R.string.fingerprint_recognized));
        ivIcon.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCallback.onAuthenticated();
            }
        }, SUCCESS_DELAY_MILLIS);
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        showError(helpString);
    }

    @Override
    public void onAuthenticationFailed() {
        showError(tvError.getContext().getString(R.string.fingerprint_not_recognized));
    }

    public void setTitle(CharSequence title) {
        this.tvTitle.setText(title);
    }

    public void setSubtitle(CharSequence subtitle) {
        this.tvSubtitle.setText(subtitle);
    }

    interface Callback {
        void onAuthenticated();

        void onError();
    }
}
