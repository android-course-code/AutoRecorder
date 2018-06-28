package me.chenjr.autorecorder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class FileItemAdapter extends RecyclerView.Adapter<FileItemAdapter.ViewHolder> {

    private List<FileItem> mFileItemList;
    static class ViewHolder extends RecyclerView.ViewHolder {
        View fileView;
        ImageView fileIcon;
        TextView fileName;
        TextView fileDel;

        public ViewHolder(View view) {
            super(view);
            fileView = view;
            fileIcon = (ImageView) view.findViewById(R.id.img_file_icon);
            fileName = (TextView) view.findViewById(R.id.tv_file_name);
            fileDel = (TextView) view.findViewById(R.id.tv_file_delete);
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
        holder.fileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Toast.makeText(v.getContext(),"Going to open "+mFileItemList.get(pos).getName(),
                        Toast.LENGTH_SHORT).show();

            }
        });
        /* 删除按钮点击事件 */
        holder.fileDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Toast.makeText(v.getContext(),mFileItemList.get(pos).getName()+" will be DELETED! ",
                        Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileItem fileItem = mFileItemList.get(position);
        holder.fileName.setText(fileItem.getName());
        holder.fileIcon.setImageResource(fileItem.getIconID());


    }

    @Override
    public int getItemCount() {
        return mFileItemList.size();
    }
}
