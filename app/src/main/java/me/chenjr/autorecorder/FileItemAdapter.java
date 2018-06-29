package me.chenjr.autorecorder;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

public class FileItemAdapter extends RecyclerView.Adapter<FileItemAdapter.ViewHolder> {

    private List<FileItem> mFileItemList;
    static class ViewHolder extends RecyclerView.ViewHolder {
        View fileView;
        ImageView fileIcon;
        TextView fileName;
        TextView fileDel;
        TextView fileLen;
        LinearLayout fileItem;

        public ViewHolder(View view) {
            super(view);
            fileView = view;
            fileIcon = view.findViewById(R.id.img_file_icon);
            fileName = view.findViewById(R.id.tv_file_name);
            fileDel = view.findViewById(R.id.tv_file_delete);
            fileLen = view.findViewById(R.id.tv_file_length);
            fileItem = view.findViewById(R.id.ll_file_item);

        }
    }

    public FileItemAdapter(List<FileItem> mFileItemList) {
        this.mFileItemList = mFileItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        /* 文件名点击事件 */
        holder.fileItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Toast.makeText(v.getContext(),"Going to open "+mFileItemList.get(pos).getName(),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://"+v.getContext().getExternalFilesDir("Record")+"/"+mFileItemList.get(pos).getName()),"audio/3gp");
                //v.getContext().startActivity(intent);



            }
        });
        /* 删除按钮点击事件 */
        holder.fileDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
//                Toast.makeText(v.getContext(),mFileItemList.get(pos).getName()+" will be DELETED! ",
//                        Toast.LENGTH_SHORT).show();
                File[] files = v.getContext().getExternalFilesDir("Record").listFiles();
                for (File file : files) {
                    if (file.getName().equals(mFileItemList.get(pos).getName())) {
                        file.delete();
                        mFileItemList.remove(pos);
                        notifyItemRemoved(pos);
                        break;
                    }
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileItem fileItem = mFileItemList.get(position);
        holder.fileName.setText(fileItem.getName());
        holder.fileIcon.setImageResource(fileItem.getIconID());
        holder.fileLen.setText(""+fileItem.getSize()+"K");


    }

    @Override
    public int getItemCount() {
        return mFileItemList.size();
    }
}
