package com.hitesh.safesoswomensafety.ui.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hitesh.safesoswomensafety.R;

import io.paperdb.Paper;

public class MessageFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_message, container, false);
        Paper.init(getContext());
        final EditText edtMsg = root.findViewById(R.id.edtMsg);
        Button btnUpdate = root.findViewById(R.id.btnUpdate);

        final String msg = Paper.book().read("message", "");
        edtMsg.setText(msg);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().write("message",edtMsg.getText().toString());
                Toast.makeText(getContext(), "Message Updated", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}