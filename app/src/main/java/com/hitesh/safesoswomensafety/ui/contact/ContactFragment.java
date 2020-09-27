package com.hitesh.safesoswomensafety.ui.contact;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hitesh.safesoswomensafety.Adapter.ContactAdapter;
import com.hitesh.safesoswomensafety.Models.Contact;
import com.hitesh.safesoswomensafety.R;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class ContactFragment extends Fragment {

    private ContactViewModel contactViewModel;
    public ContactAdapter contactAdapter;
    public List<Contact> contactList = new ArrayList<>();
    private RecyclerView recyContact;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contactViewModel =
                ViewModelProviders.of(this).get(ContactViewModel.class);
        Paper.init(getContext());

        View root = inflater.inflate(R.layout.fragment_contact, container, false);
        recyContact = root.findViewById(R.id.recyContact);
        Button btnAddContact = root.findViewById(R.id.btnAddContact);

        recyContact.setLayoutManager(new LinearLayoutManager(getContext()));
        recyContact.setHasFixedSize(true);
        loadContact();
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });


        contactAdapter.setOnDeleteListener(new ContactAdapter.onDeleteListener() {
            @Override
            public void onDelete(int pos) {
                if (contactList.size()>0){
                    contactList.remove(pos);
                    Paper.book().write("contact",contactList);
                    contactAdapter.notifyDataSetChanged();
                }
            }
        });

        return root;
    }

    private void loadContact() {
        contactList = Paper.book().read("contact", new ArrayList<Contact>());
        contactAdapter = new ContactAdapter(getContext(), contactList);
        recyContact.setAdapter(contactAdapter);
    }

    private void showAddDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        dialog.setContentView(R.layout.dialog_add_contact);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final EditText edtName = dialog.findViewById(R.id.edtName);
        final EditText edtNumber = dialog.findViewById(R.id.edtNumber);
        Button btnAddContact = dialog.findViewById(R.id.btnAdd);

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Contact> contacts= Paper.book().read("contact", new ArrayList<Contact>());
                String name = edtName.getText().toString();
                String number = edtNumber.getText().toString();
                if (!name.isEmpty() && !number.isEmpty()){
                    Contact contact = new Contact(number, name);
                    contacts.add(contact);
                    Paper.book().write("contact", contacts);
                    contactList.add(contact);
                    contactAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                    showCustomDialog();
                }else {
                    Toast.makeText(getContext(), "All Fields Are Required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();


    }

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = getActivity().findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.success_dialog, viewGroup, false);

        Button btnOk = dialogView.findViewById(R.id.buttonOk);
        TextView txtMsg = dialogView.findViewById(R.id.txtMsg);
        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        txtMsg.setText("Emergency Contact Added");
        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
}