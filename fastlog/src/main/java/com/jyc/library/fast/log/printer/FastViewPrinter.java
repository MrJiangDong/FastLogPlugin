package com.jyc.library.fast.log.printer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jyc.library.fast.log.FastLogConfig;
import com.jyc.library.fast.log.FastLogType;
import com.jyc.library.fast.log.R;
import com.jyc.library.fast.log.bean.FastLogModel;

import java.util.ArrayList;
import java.util.List;

/**
 * FastViewPrinter 将log显示在界面上
 */
public class FastViewPrinter implements FastLogPrinter {
    private final RecyclerView recyclerView;
    private final LogAdapter adapter;
    private final FastViewPrinterProvider viewPrinterProvider;

    public FastViewPrinter(Activity activity) {
        FrameLayout rootView = activity.findViewById(android.R.id.content);
        recyclerView = new RecyclerView(activity);
        adapter = new LogAdapter(LayoutInflater.from(recyclerView.getContext()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        viewPrinterProvider = new FastViewPrinterProvider(rootView, recyclerView);
    }

    /**
     * 获取ViewProvider,通过ViewProvider可以控制log视图的展示和隐藏
     *
     * @return ViewProvider
     */
    @NonNull
    public FastViewPrinterProvider getViewProvider() {
        return viewPrinterProvider;
    }

    @Override
    public void print(@NonNull FastLogConfig config, int level, String tag, @NonNull String printString) {
        //将log展示添加到recyclerView
        adapter.addItem(new FastLogModel(System.currentTimeMillis(), level, tag, printString));
        //滚动到对应的位置
        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    private static class LogAdapter extends RecyclerView.Adapter<LogViewHolder> {

        private LayoutInflater inflater;
        private List<FastLogModel> logs = new ArrayList<>();

        public LogAdapter(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        void addItem(FastLogModel logItem) {
            logs.add(logItem);
            notifyItemInserted(logs.size() - 1);
        }

        @NonNull
        @Override
        public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.adapter_fastlog_item, parent, false);
            return new LogViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
            FastLogModel logModel = logs.get(position);

            int color = getHighlightColor(logModel.level);
            holder.tagView.setTextColor(color);
            holder.messageView.setTextColor(color);

            holder.tagView.setText(logModel.getFlattened());
            holder.messageView.setText(logModel.log);
        }

        /**
         * 根据log级别获取不同的log颜色
         *
         * @param logLevel log 级别
         * @return 高亮的颜色
         */
        private int getHighlightColor(int logLevel) {
            int highlight;
            switch (logLevel) {
                case FastLogType.V:
                    highlight = 0xffFDFFFB;
                    break;

                case FastLogType.D:
                    highlight = 0xff54CEE3;
                    break;

                case FastLogType.I:
                    highlight = 0xff55E350;
                    break;

                case FastLogType.W:
                    highlight = 0xffF8DA3F;
                    break;

                case FastLogType.E:
                    highlight = 0xffFF5370;
                    break;

                default:
                    highlight = 0xffFF9492;
                    break;
            }
            return highlight;
        }

        @Override
        public int getItemCount() {
            return logs.size();
        }
    }

    private static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tagView;
        TextView messageView;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tagView = itemView.findViewById(R.id.tag);
            messageView = itemView.findViewById(R.id.message);
        }
    }

}
