package com.example.rschmitt.messageencryptor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
{
    @Bind(R.id.Message)
    TextView textView_Message;
    @Bind(R.id.Output)
    TextView textView_Output;
    @Bind(R.id.Encrypt_Button)
    Button button_Encrypt;
    @Bind(R.id.Decrypt_Button)
    Button button_Decrypt;
    @Bind(R.id.Share_Checkbox)
    AppCompatCheckBox checkBox_Share;
    @Bind(R.id.Copy_To_Clipboard_Checkbox)
    AppCompatCheckBox checkBox_CopyToClipboard;
    @Bind(R.id.Decrypt_on_startup_Checkbox)
    AppCompatCheckBox checkBox_DecryptOnStartup;
    @Bind(R.id.container)
    CoordinatorLayout container;

    private Cryptor cryptor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        LoadSettings();
        cryptor = new Cryptor();

        button_Encrypt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Crypt(true);
            }
        });

        button_Decrypt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Crypt(false);
            }
        });

        CopyFromClipboard();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LoadSettings();
        CopyFromClipboard();
    }

    public void Crypt(boolean encrypt)
    {
        String message = String.valueOf(textView_Message.getText());
        String output;

        if (encrypt)
            output = (cryptor.encrypt(message));
        else
            output = (cryptor.decrypt(message));

        textView_Output.setText(output);

        if (encrypt)
        {
            if (checkBox_Share.isChecked())
                Share(output);
            if (checkBox_CopyToClipboard.isChecked())
                CopyToClipboard(output);
        }
    }

    public void CopyToClipboard(String message)
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Output", message);
        clipboard.setPrimaryClip(clip);

        showSnackbar(R.string.copied_to_clipboard);
    }

    public void CopyFromClipboard()
    {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip())
        {
            textView_Message.setText(clipboard.getText());
            if (checkBox_DecryptOnStartup.isChecked()) ;
            Crypt(false);
        } else
            showSnackbar(R.string.clipboard_is_empty);
    }

    public void Share(String message)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void showSnackbar(int stringResource)
    {
        Snackbar.make(container, stringResource, Snackbar.LENGTH_SHORT).show();
    }

    private void SaveSettings()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("Share", checkBox_Share.isChecked()).apply();
        prefs.edit().putBoolean("CopyToClipboard", checkBox_CopyToClipboard.isChecked()).apply();
        prefs.edit().putBoolean("DecryptOnStartup", checkBox_DecryptOnStartup.isChecked()).apply();
    }

    private void LoadSettings()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.contains("Share"))
        checkBox_Share.setChecked(prefs.getBoolean("Share", false));
        if (prefs.contains("CopyToClipboard"))
        checkBox_CopyToClipboard.setChecked(prefs.getBoolean("CopyToClipboard", false));
        if (prefs.contains("DecryptOnStartup"))
        checkBox_DecryptOnStartup.setChecked(prefs.getBoolean("DecryptOnStartup", false));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        SaveSettings();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        SaveSettings();
    }
}
