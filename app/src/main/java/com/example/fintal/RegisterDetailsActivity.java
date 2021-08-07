package com.example.fintal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.fintal.Helpers.BitmapScaler;
import com.example.fintal.Helpers.DeviceDimensionsHelper;
import com.example.fintal.Models.Category;
import com.example.fintal.Models.Register;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class RegisterDetailsActivity extends AppCompatActivity {

    public static final String TAG = "RegisterDetailsActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 350;

    private Register register;
    private TextView tvDescription;
    private TextView tvCategory;
    private TextView tvAmount;
    private ImageView ivIcon;
    private ImageView ivTicket;
    private ImageButton btnBack;
    private TextView tvDate;
    private Button btnEdit;
    private ConstraintLayout clEdit;

    //Edit variables
    List<Category> categories;
    ArrayList<String> categoriesString;
    AutoCompleteTextView categoryPicker;
    TextInputEditText etDescription;
    Button btnTicket;
    Button btnSave;

    private File photoFile;
    public String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_details);

        //Get Views by id
        tvDescription = findViewById(R.id.tvDescriptionDetails);
        tvCategory = findViewById(R.id.tvCategoryDetails);
        tvAmount = findViewById(R.id.tvAmountDetails);
        ivIcon = findViewById(R.id.ivIconDetails);
        ivTicket = findViewById(R.id.ivTicket);
        btnBack = findViewById(R.id.btnBackDetails);
        tvDate = findViewById(R.id.tvDateDetails);
        btnEdit = findViewById(R.id.btnEdit);
        clEdit = findViewById(R.id.clEditLayout);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });

        //Get register from Parceler wrap
        register = Parcels.unwrap(getIntent().getParcelableExtra(Register.class.getSimpleName()));

        //Bind register data to Views
        tvDescription.setText(register.getDescription());
        tvCategory.setText(register.getCategory().getString("name"));
        tvAmount.setText("$" + register.getAmount());
        ParseFile fileIcon = register.getCategory().getParseFile("iconFile");
        ParseFile ticketPhoto = register.getPhoto();
        if (fileIcon != null) {
            GlideToVectorYou.init().with(this).load(Uri.parse(fileIcon.getUrl()), ivIcon);
        }
        if (ticketPhoto != null) {
            Glide.with(this).load(ticketPhoto.getUrl()).into(ivTicket);
        }
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = format.format(register.getValueDate());
        tvDate.setText(dateString);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clEdit.setVisibility(View.VISIBLE);
            }
        });

        etDescription = findViewById(R.id.etDescriptionEdit);

        //Initialize arrays for categories
        categories = new ArrayList<>();
        categoriesString = new ArrayList<>();
        getCategories();

        //Set adapter for dropdown menu on TextInputLayout
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item, categoriesString);
        categoryPicker = findViewById(R.id.categoryEdit);
        categoryPicker.setAdapter(adapter);

        //Set on click listener to ADD TICKET BUTTON
        btnTicket = findViewById(R.id.btnTicketEdit);
        btnTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        btnSave = findViewById(R.id.btnSaveEdit);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getEditableText().toString();
                updateRegister(description);
            }
        });
    }

    private void updateRegister(String description) {
        String textCategory = categoryPicker.getEditableText().toString();
        int index;
        if (categoriesString.contains(textCategory)) {
            index = categoriesString.indexOf(textCategory);
        } else {
            String currentCategory = tvCategory.getText().toString();
            index = categoriesString.indexOf(currentCategory);
        }

        String descriptionString;
        if (description.isEmpty()) {
            descriptionString = tvDescription.getText().toString();
        } else {
            descriptionString = description;
        }

        ParseQuery<Register> query = ParseQuery.getQuery(Register.class);
        int finalIndex = index;
        query.getInBackground(register.getObjectId(), new GetCallback<Register>() {
            @Override
            public void done(Register object, ParseException e) {
                if (e == null) {
                    BitmapDrawable drawable = (BitmapDrawable) ivTicket.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    byte[] pfArray = getBytesFromBitmap(bitmap);
                    ParseFile file = new ParseFile("abc.png", pfArray);
                    object.put("filePhoto", file);
                    object.put("category" , categories.get(finalIndex));
                    object.put("description", descriptionString);
                    object.saveInBackground();
                    setResult(126);
                    finish();
                }
            }
        });
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }

    //Function to get categories
    private void getCategories() {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.addAscendingOrder("createdAt");
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue getting categories", e);
                    return;
                }
                categories.addAll(objects);
                for (int i = 0; i < categories.size(); i++) {
                    //Add only Name string of each Category object
                    categoriesString.add(categories.get(i).getName());
                }
            }
        });
    }

    //CAMERA LAUNCHER FUNCTIONS
    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);
        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getApplicationContext(), "com.codepath.fileprovider.fintal", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, Integer.toString(resultCode));
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.d(TAG, Integer.toString(resultCode));
            if (resultCode == RESULT_OK) {
                //Get screenWidth for scaling
                int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getApplicationContext());
                // by this point we have the camera photo on disk
                Bitmap rawImage = rotateBitmapOrientation(photoFile.getAbsolutePath());
                Bitmap scaledImage = BitmapScaler.scaleToFitHeight(rawImage, screenWidth);

                // Configure byte output stream
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                scaledImage.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                ivTicket.setImageBitmap(scaledImage);
            } else { // Result was a failure
                Toast.makeText(getApplicationContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}