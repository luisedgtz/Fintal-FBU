package com.example.fintal;

import android.content.Intent;

public interface OnIntentReceived {
    void onIntent(Intent i, int resultCode);
}