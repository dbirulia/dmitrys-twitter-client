package com.birulia.apps.dmitrytwitterclient.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.birulia.apps.dmitrytwitterclient.TwitterApplication;
import com.birulia.apps.dmitrytwitterclient.utils.TwitterClient;
import com.birulia.apps.dmitrytwitterclient.models.User;
import com.bumptech.glide.Glide;
import com.codepath.apps.dmitrytwitterclient.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class PostTweetDialogFragment extends DialogFragment {

    private EditText etMessage;
    private ImageButton btnSave;
    private ImageButton btnCancel;
    private TwitterClient client;
    private ImageView ivMyAvatar;
    private TextView tvUsername;
    private TextView tvFullName;
    private TextView tvCharsLeft;

    private String myName;
    private String myImgURL;

    public PostTweetDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static PostTweetDialogFragment newInstance(String title) {
        PostTweetDialogFragment frag = new PostTweetDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSave = (ImageButton) view.findViewById(R.id.btnSave);
        btnCancel = (ImageButton) view.findViewById(R.id.ibCross);
        etMessage = (EditText) view.findViewById(R.id.etTweet);

        ivMyAvatar = (ImageView) view.findViewById(R.id.ivMyAvatar);
        tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        tvFullName = (TextView) view.findViewById(R.id.tvFullName);
        tvCharsLeft = (TextView) view.findViewById(R.id.tvCharsLeft);

        btnSave.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           client = TwitterApplication.getRestClient();
                                           client.createTweet(etMessage.getText().toString(), new JsonHttpResponseHandler() {

                                               @Override
                                               public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                   Log.w("TwitterApp", "All good posted tweet");
                                                   dismiss();
                                               }

                                               @Override
                                               public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                   // log exception
                                                   Log.w("TwitterApp", "failed to post tweet");
                                                   dismiss();
                                               }
                                           });
                                       }
                                   });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        client = TwitterApplication.getRestClient();
        client.getMyProfile(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                User myself = new User(response);

                Glide.with(getContext())
                        .load(myself.getProfileImageURL())
                        .bitmapTransform(new RoundedCornersTransformation(getContext(), 5, 2))
                        .error(R.drawable.empty_profile)
                        .placeholder(R.drawable.empty_profile)
                        .into(ivMyAvatar);

                tvUsername.setText(myself.getScreenName());
                tvFullName.setText(myself.getName());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // log exception
                Log.w("TwitterApp", "failed to post tweet");
                dismiss();
            }
        });


        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = s.toString().length();
                int left = 140 - number;
                tvCharsLeft.setText(Integer.valueOf(left).toString() + " Chars left");
            }
        });

    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }


}

