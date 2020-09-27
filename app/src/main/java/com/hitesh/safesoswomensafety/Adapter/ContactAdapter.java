package com.hitesh.safesoswomensafety.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hitesh.safesoswomensafety.Models.Contact;
import com.hitesh.safesoswomensafety.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    Context ctx;
    List<Contact> contactList;
    public onDeleteListener onDeleteListener;
    public ContactAdapter(Context ctx, List<Contact> contactList) {
        this.ctx = ctx;
        this.contactList = contactList;

    }

    public ContactAdapter.onDeleteListener getOnDeleteListener() {
        return onDeleteListener;
    }

    public void setOnDeleteListener(ContactAdapter.onDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public interface onDeleteListener{
        void onDelete(int pos);
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.layout_contact_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Contact contact = contactList.get(position);
        holder.txtName.setText(contact.name);
        holder.txtNumber.setText(contact.number);

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteListener.onDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNumber;
        ImageView imgDelete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            imgDelete = itemView.findViewById(R.id.imgDelete);

        }
    }
}
