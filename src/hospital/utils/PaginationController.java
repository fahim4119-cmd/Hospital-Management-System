package hospital.utils;

import javax.swing.*;
import java.awt.*;

public class PaginationController {
    public interface PageLoader {
        void loadPage(int page, int pageSize);
    }

    private int page = 0;
    private int totalRows = 0;
    private final int pageSize;
    private final JLabel label = new JLabel();
    private final JButton prev = UITheme.createPrimaryButton("Prev");
    private final JButton next = UITheme.createPrimaryButton("Next");
    private final PageLoader loader;

    public PaginationController(int pageSize, PageLoader loader) {
        this.pageSize = pageSize;
        this.loader = loader;
        prev.addActionListener(e -> {
            if (page > 0) {
                page--;
                loader.loadPage(page, pageSize);
                refresh();
            }
        });
        next.addActionListener(e -> {
            if ((page + 1) * pageSize < totalRows) {
                page++;
                loader.loadPage(page, pageSize);
                refresh();
            }
        });
        prev.setPreferredSize(new Dimension(78, 32));
        next.setPreferredSize(new Dimension(78, 32));
        label.setFont(UITheme.FONT_BODY);
        label.setForeground(UITheme.TEXT_MUTED);
    }

    public JPanel panel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.setOpaque(false);
        panel.add(prev);
        panel.add(label);
        panel.add(next);
        return panel;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
        if (page * pageSize >= totalRows) page = 0;
        refresh();
    }

    public void reset() {
        page = 0;
        refresh();
    }

    public int getPage() {
        return page;
    }

    private void refresh() {
        int pages = Math.max(1, (int) Math.ceil(totalRows / (double) pageSize));
        label.setText("Page " + (page + 1) + " of " + pages);
        prev.setEnabled(page > 0);
        next.setEnabled((page + 1) * pageSize < totalRows);
    }
}
