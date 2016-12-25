package eu.marcocattaneo.rememberhere.presentation.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import eu.marcocattaneo.rememberhere.R;

public class EditTextDialog extends AlertDialog.Builder {

    private EditText editText;

    private DialogInterface mDialogInterface;

    public EditTextDialog(@NonNull Context context) {
        super(context);

        init();
    }

    public EditTextDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);

        init();
    }


    private void init() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edittext, null);
        setView(view);

        editText = (EditText) view.findViewById(R.id.edittext);

        setCancelable(false);
    }

    public void setConfirmDialog(String label, DialogInterface dialogInterface) {
        this.mDialogInterface = dialogInterface;
        setPositiveButton(label, new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                mDialogInterface.onClick(EditTextDialog.this);
            }
        });
    }

    public String getValue() {
        return editText.getText().toString();
    }

    public interface DialogInterface {

        void onClick(EditTextDialog dialog);

    }

}
