package com.blupie.technotweets.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blupie.technotweets.R;
import com.blupie.technotweets.models.PrefManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class StartActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    @BindView(R.id.startActivityLayout)
    RelativeLayout startActivityLayout;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.layoutDots)
    LinearLayout layoutDots;
    //    @BindView(R.id.spin_kit)
//    SpinKitView spinKit;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private GoogleApiClient mGoogleSignInClient;
    TextView googleButton;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mref;
    ProgressDialog progressDialog;

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    int current, f;
    private FloatingActionButton fbut;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
        f = prefs.getInt("persistent", 0);
        if (f == 0) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } else {
            prefs.edit().putInt("persistent", 0).apply();
        }

        // Checking for first time launch - before calling setContentView()
        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference().child("root").child("twitter").child("users");


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        open();


        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        fbut = findViewById(R.id.fbat);


        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome1slider,
                R.layout.welcome2slider,
                R.layout.welcome3slider};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);


        fbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // checking for last page
                // if last page home screen will be launched
                current = getItem(+1);
                viewPager.setCurrentItem(current);
                if (current == 2) {
                    // move to next screen
                    fbut.setImageResource(R.drawable.check_white);

                } else if (current >= layouts.length) {
                    prefManager.setFirstTimeLaunch(false);
                    clickme();
                    fbut.setClickable(false);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final SharedPreferences prefs = getSharedPreferences(
                "login", MODE_PRIVATE);
        int flag = prefs.getInt("loginvar", 0);
        if (flag == 1) {
            Intent intent = new Intent(this, mainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {

        if (isNetworkConnected()) {

            updateUI(user);
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(StartActivity.this);
            dialog.setTitle("Connectino Error ");
            dialog.setCancelable(false);
            dialog.setMessage("Unable to connect with the server.\n Check your Internet connection and try again.");
            dialog.setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    launchHomeScreen();
                }
            }).show();
        }
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {

                fbut.setImageResource(R.drawable.check_white);
                // last page. make button text to GOT IT
            } else {
                fbut.setImageResource(R.drawable.arrow_white);
                // still pages are left
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public void clickme() {
        if (isNetworkConnected()) {
//            Style style = Style.values()[4];
//            Sprite drawable = SpriteFactory.create(style);
//            spinKit.setIndeterminateDrawable(drawable);
//            spinKit.setVisibility(View.VISIBLE);
            avi.show();
            layoutDots.setVisibility(View.INVISIBLE);
            signIn();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(StartActivity.this);
            dialog.setTitle("Connectino Error ");
            dialog.setCancelable(false);
            dialog.setMessage("Unable to connect with the server.\n Check your Internet connection and try again.");
            dialog.setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    clickme();
                }
            }).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void signIn() {

        mGoogleSignInClient.clearDefaultAccountAndReconnect();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
//                spinKit.setVisibility(View.GONE);
                avi.hide();
                layoutDots.setVisibility(View.VISIBLE);
                // Google Sign In failed, update UI appropriately
                Snackbar snackbar1 = Snackbar.make(startActivityLayout, "Authentication Failed", Snackbar.LENGTH_SHORT);
                fbut.setClickable(true);
                snackbar1.show();
                // ...
            }
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            SharedPreferences prefs = getSharedPreferences(
                    "login", MODE_PRIVATE);
            prefs.edit().putInt("loginvar", 1).apply();

            Intent intent = new Intent(this, mainActivity.class);
            startActivity(intent);
            finish();
        } else {

        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser user = mAuth.getCurrentUser();
                            final String a = user.getUid().toString();
                            final String url = user.getPhotoUrl().toString();
                            if (url != null) {
                                mref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(a)) {
                                            if (dataSnapshot.child(a).hasChild("profileimage")) {
//                                                    spinKit.setVisibility(View.GONE);
                                                avi.hide();
                                                layoutDots.setVisibility(View.VISIBLE);
                                                Toast.makeText(StartActivity.this, "Welcome Back " + user.getDisplayName().toString(), Toast.LENGTH_SHORT).show();
                                                updateUI(user);
                                            }
                                        } else {

                                            mref.child(a).child("profileimage").setValue(url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mref.child(a).child("email").setValue(user.getEmail().toString());
                                                    mref.child(a).child("name").setValue(user.getDisplayName().toString());
                                                    mref.child(a).child("uid").setValue(user.getUid().toString());
                                                    mref.child(a).child("value").setValue("0");
//                                                        spinKit.setVisibility(View.GONE);
                                                    avi.hide();
                                                    layoutDots.setVisibility(View.VISIBLE);
                                                    updateUI(user);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(StartActivity.this, "Photo not uploaded", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        } else {
                            updateUI(null);
                            Snackbar snackbar1 = Snackbar.make(startActivityLayout, "Login with College ID", Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                            fbut.setClickable(true);
//                                spinKit.setVisibility(View.GONE);
                            avi.hide();
                            layoutDots.setVisibility(View.VISIBLE);
                        }

                        // ...
                    }
                });
//
    }


    public void open() {
        if (checkPermission()) {
        } else {
            requestPermission();
        }
    }

    public Boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        int requestCode;
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, INTERNET}, requestCode = 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, INTERNET},
                                                        1);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .create()
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        int pid = Process.myPid();
        Process.killProcess(pid);

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        int pid=android.os.Process.myPid();
//        android.os.Process.killProcess(pid);
//    }


}
