package com.nyinyihtunlwin.projects.fingerprintsample;

import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FingerprintDialog extends DialogFragment implements FingerprintController.Callback {


    public static final String FRAGMENT_TAG = FingerprintDialog.class.getSimpleName();
    private static final String DEFAULT_KEY_NAME = "default_key";

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_sub_title)
    TextView tvSubTitle;

    @BindView(R.id.tv_cancel)
    TextView btnCancel;

    @BindView(R.id.iv_fingerPrint)
    ImageView ivFingerPrint;

    private FingerprintManagerCompat.CryptoObject cryptoObject = null;
    private KeyStore keyStore = null;
    private KeyGenerator keyGenerator = null;
    private FingerprintController mFingerprintController = null;

    private FDCallback mFDCallback;

    public static FingerprintDialog newInstance() {
        Bundle args = new Bundle();
        FingerprintDialog fragment = new FingerprintDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fingerprint, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFingerprintController = new FingerprintController(
                FingerprintManagerCompat.from(getContext()),
                this,
                tvTitle,
                ivFingerPrint
        );
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFDCallback.onCancelClicked();
                dismiss();
            }
        });
        setCancelable(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }

        try {
            keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createKey(DEFAULT_KEY_NAME, false);
        }

        Cipher defaultCipher;
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (initCipher(defaultCipher, DEFAULT_KEY_NAME)) {
                cryptoObject = new FingerprintManagerCompat.CryptoObject(defaultCipher);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if (cryptoObject != null) {
            mFingerprintController.startListening(cryptoObject);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintController.stopListening();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        } catch (CertificateException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        try {
            keyStore.load(null);
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void setListener(FDCallback fdCallback) {
        this.mFDCallback = fdCallback;
    }

    @Override
    public void onAuthenticated() {
        mFDCallback.onSuccess();
        dismiss();
    }

    @Override
    public void onError() {

    }

    interface FDCallback {
        void onSuccess();

        void onCancelClicked();
    }
}
