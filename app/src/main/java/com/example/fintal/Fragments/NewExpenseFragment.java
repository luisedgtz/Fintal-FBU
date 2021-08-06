package com.example.fintal.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fintal.Helpers.BitmapScaler;
import com.example.fintal.Helpers.DeviceDimensionsHelper;
import com.example.fintal.Models.Category;
import com.example.fintal.Models.Register;
import com.example.fintal.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewExpenseFragment extends DialogFragment {
    public static final String TAG = "NewExpenseFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 150;

    TextInputEditText etDescription;
    TextInputEditText etAmount;
    List<Category> categories;
    ArrayList<String> categoriesString;
    AutoCompleteTextView categoryPicker;
    BottomSheetDialog bottomSheetDialog;
    Button btnTicket;
    Button btnSave;
    Button btnCancel;

    private File photoFile;
    public String photoFileName = "photo.jpg";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_expense, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }

        etDescription = getView().findViewById(R.id.etDescriptionExpense);
        etAmount = getView().findViewById(R.id.etAmountExpense);

        //Initialize arrays for categories
        categories = new ArrayList<>();
        categoriesString = new ArrayList<>();
        getCategories();

        //Set adapter for dropdown menu on TextInputLayout
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.list_item, categoriesString);
        categoryPicker = getView().findViewById(R.id.categoryExpense);
        categoryPicker.setAdapter(adapter);

        bottomSheetDialog = new BottomSheetDialog(getContext());

        //Set on click listener to ADD TICKET BUTTON
        btnTicket = getView().findViewById(R.id.btnTicketExpense);
        btnTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        //Set on click listener to SAVE BUTTON
        btnSave = getView().findViewById(R.id.btnSaveExpense);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getEditableText().toString();
                if (description.isEmpty() || etAmount.getEditableText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Description and amount cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                Number amount =  Double.parseDouble(etAmount.getEditableText().toString());
                ParseUser currentUser = ParseUser.getCurrentUser();
                saveExpense(description, amount, currentUser, photoFile);
            }
        });

        //Set on click listener to CANCEL BUTTON
        btnCancel = getView().findViewById(R.id.btnCancelExpense);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void saveExpense(String description, Number amount, ParseUser currentUser, File photoFile) {
        String textCategory = categoryPicker.getEditableText().toString();
        int index = 0;
        if (categoriesString.contains(textCategory)) {
            index = categoriesString.indexOf(textCategory);
        }
        Register register = new Register();
        register.setType(false);
        register.setUser(currentUser);
        register.setCreatedAt(new Date());
        if (photoFile != null) {
            register.setPhoto(new ParseFile(photoFile));
        }
        register.setAmount(amount);
        register.setCategory(categories.get(index));
        register.setDescription(description);
        register.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "error", e);
                    return;
                }
                changeBalance(amount);
                Toast.makeText(getContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    private void changeBalance(Number amount) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    Number expenses = object.getNumber("totalExpenses");
                    expenses = expenses.floatValue() + amount.floatValue();
                    object.put("totalExpenses", expenses);
                    object.saveInBackground();
                }
            }
        });
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

    //Function to show bottom sheet dialog
    private void showBottomSheetDialog() {
        bottomSheetDialog.setContentView(R.layout.fragment_options_bottom_sheet);

        ConstraintLayout btnTakePhoto = bottomSheetDialog.findViewById(R.id.btnTakePhoto);
        ConstraintLayout btnGallery = bottomSheetDialog.findViewById(R.id.btnGallery);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked");
            }
        });
        bottomSheetDialog.show();
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
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.fintal", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                //Get screenWidth for scaling
                int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());
                // by this point we have the camera photo on disk
                Bitmap rawImage = rotateBitmapOrientation(photoFile.getAbsolutePath());
                Bitmap scaledImage = BitmapScaler.scaleToFitHeight(rawImage, screenWidth);

                // Configure byte output stream
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                scaledImage.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                ImageView ivPreview = getView().findViewById(R.id.ivTicketExpense);
                ivPreview.setImageBitmap(scaledImage);
                bottomSheetDialog.dismiss();
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
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
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}