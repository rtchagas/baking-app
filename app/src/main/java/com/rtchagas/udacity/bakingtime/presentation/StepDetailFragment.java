package com.rtchagas.udacity.bakingtime.presentation;

import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.rtchagas.udacity.bakingtime.R;
import com.rtchagas.udacity.bakingtime.core.Step;

import java.security.InvalidParameterException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Step detail screen.
 * This fragment is either contained in a {@link StepsListActivity}
 * in two-pane mode (on tablets) or a {@link StepDetailActivity}
 * on handsets.
 */
public class StepDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_STEP = "arg_step";

    private static final String STATE_PLAYER_WINDOW = "state_player_window";
    private static final String STATE_PLAYER_POSITION = "state_player_position";

    // Views
    @BindView(R.id.layout_root_fragment_step)
    ConstraintLayout mRootView;

    @BindView(R.id.player_view)
    PlayerView mExoPlayerView;

    @BindView(R.id.text_step_description)
    TextView mTextStepDescription;

    private Step mStep = null;

    private ExoPlayer mExoPlayer = null;
    private int mPlayerCurrentWindow = C.INDEX_UNSET;
    private long mPlayerCurrentPosition = C.TIME_UNSET;

    private boolean mIsTwoPane = false;

    private Dialog mFullScreenDialog = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the step specified by the fragment arguments.
        mStep = (Step) getArguments().getSerializable(ARG_STEP);
        if (mStep == null) {
            throw new InvalidParameterException("Arguments[Step] must not be null!");
        }

        // Restore player's position, if available.
        if (savedInstanceState != null) {
            mPlayerCurrentWindow = savedInstanceState.getInt(STATE_PLAYER_WINDOW);
            mPlayerCurrentPosition = savedInstanceState.getLong(STATE_PLAYER_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Check two-pane mode
        mIsTwoPane = (getActivity().findViewById(R.id.step_detail_container_composite) != null);

        // Initialize the Player view
        if (TextUtils.isEmpty(mStep.getVideoURL())) {
            mExoPlayerView.setVisibility(View.GONE);
        }
        else {
            mExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(
                    getResources(), R.drawable.img_cooking_step));
        }

        // Fill the step details
        mTextStepDescription.setText(mStep.getDescription());
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeExoPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        // If activity is not visible, we can dismiss the fullscreen dialog.
        if (mFullScreenDialog != null) {
            mFullScreenDialog.dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_PLAYER_WINDOW, mPlayerCurrentWindow);
        outState.putLong(STATE_PLAYER_POSITION, mPlayerCurrentPosition);
    }

    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar) {
            @Override
            public void onBackPressed() {
                // Finish the activity.
                getActivity().finish();
            }
        };

        // Set it full screen immersive
        Window window = mFullScreenDialog.getWindow();
        if (window != null) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    private void openFullscreenDialog() {

        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);

        mFullScreenDialog.addContentView(mExoPlayerView,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        mFullScreenDialog.show();
    }

    private void initializeExoPlayer() {

        if (TextUtils.isEmpty(mStep.getVideoURL())) {
            // This step has no video...
            return;
        }

        if (mExoPlayer == null) {

            // Init the fullscreen dialog
            initFullscreenDialog();

            // Get the step video URI
            Uri mediaUri = Uri.parse(mStep.getVideoURL());

            // 1. Create a default TrackSelector
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            // 2. Create the player
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

            // 3. Attach the player to the view
            mExoPlayerView.setPlayer(mExoPlayer);

            // 4. Prepare the media source
            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                    Util.getUserAgent(getActivity(), getActivity().getPackageName()), bandwidthMeter);
            // This is the MediaSource representing the media to be played.
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaUri);
            // Prepare the player with the source.
            mExoPlayer.prepare(videoSource);
            mExoPlayer.setPlayWhenReady(true);
        }

        // Restore the position, if available.
        boolean haveResumePosition = (mPlayerCurrentWindow != C.INDEX_UNSET);
        if (haveResumePosition) {
            mExoPlayer.seekTo(mPlayerCurrentWindow, mPlayerCurrentPosition);
        }

        // Check if need to enter in fullscreen video mode
        boolean isFullscreenMode = (!mIsTwoPane && getResources().getBoolean(R.bool.is_landscape));
        if (isFullscreenMode) {
            openFullscreenDialog();
        }
    }

    private void releasePlayer() {

        if (mExoPlayer != null) {

            // Save the current position
            mPlayerCurrentWindow = mExoPlayer.getCurrentWindowIndex();
            mPlayerCurrentPosition = mExoPlayer.getCurrentPosition();

            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }
}
