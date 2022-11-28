package com.acc.common;

import com.common.SelectionObserver;
import com.common.Util1;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class DateTableDecorator {

    private final JPanel contentPanel;
    private final int currentPageSize = 1;
    private int currentPage = 1;
    private JPanel pageLinkPanel;
    private static final int MAX_PAGING_TO_SHOW = 9;
    private static final String ELLIPSES = "...";
    private HashMap<Integer, String> hmData;
    private HashMap<String, Integer> hmPage;

    public HashMap<String, Integer> getHmPage() {
        return hmPage;
    }

    public void setHmPage(HashMap<String, Integer> hmPage) {
        this.hmPage = hmPage;
    }

    private SelectionObserver observer;

    public SelectionObserver getObserver() {
        return observer;
    }

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public HashMap<Integer, String> getHmData() {
        return hmData;
    }

    public void setHmData(HashMap<Integer, String> hmData) {
        this.hmData = hmData;
    }

    public DateTableDecorator(JPanel contentPanel) {
        this.contentPanel = contentPanel;
    }

    public static DateTableDecorator decorate(JPanel contentPanel) {
        DateTableDecorator decorator = new DateTableDecorator(contentPanel);
        decorator.init();
        return decorator;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    private void init() {
        initPaginationComponents();
    }

    private void initPaginationComponents() {
        contentPanel.setLayout(new BorderLayout());
        JPanel paginationPanel = createPaginationPanel();
        contentPanel.add(paginationPanel, BorderLayout.NORTH);
    }

    private JPanel createPaginationPanel() {
        JPanel paginationPanel = new JPanel();
        pageLinkPanel = new JPanel(new GridLayout(1, MAX_PAGING_TO_SHOW, 3, 3));
        paginationPanel.add(pageLinkPanel);
        return paginationPanel;
    }

    public void refreshButton(String date) {
        pageLinkPanel.removeAll();
        if (!date.equals("-")) {
            Integer index = hmPage.get(date);
            if (index == null) {
                currentPage = 1;
            } else {
                currentPage = index;
            }
        }
        int totalRows = hmData.size();
        int pages = (int) Math.ceil((double) totalRows / currentPageSize);
        ButtonGroup buttonGroup = new ButtonGroup();
        if (pages > MAX_PAGING_TO_SHOW) {
            addPageButton(pageLinkPanel, buttonGroup, 1);
            if (currentPage > (pages - ((MAX_PAGING_TO_SHOW + 1) / 2))) {
                //case: 1 ... n->lastPage
                pageLinkPanel.add(createEllipsesComponent());
                addPageButtonRange(pageLinkPanel, buttonGroup, pages - MAX_PAGING_TO_SHOW + 3, pages);
            } else if (currentPage <= (MAX_PAGING_TO_SHOW + 1) / 2) {
                //case: 1->n ...lastPage
                addPageButtonRange(pageLinkPanel, buttonGroup, 2, MAX_PAGING_TO_SHOW - 2);
                pageLinkPanel.add(createEllipsesComponent());
                addPageButton(pageLinkPanel, buttonGroup, pages);
            } else {//case: 1 .. x->n .. lastPage
                pageLinkPanel.add(createEllipsesComponent());//first ellipses
                //currentPage is approx mid point among total max-4 center links
                int start = currentPage - (MAX_PAGING_TO_SHOW - 4) / 2;
                int end = start + MAX_PAGING_TO_SHOW - 5;
                addPageButtonRange(pageLinkPanel, buttonGroup, start, end);
                pageLinkPanel.add(createEllipsesComponent());//last ellipsis
                addPageButton(pageLinkPanel, buttonGroup, pages);//last page link
            }
        } else {
            addPageButtonRange(pageLinkPanel, buttonGroup, 1, pages);
        }
        pageLinkPanel.getParent().validate();
        pageLinkPanel.getParent().repaint();

    }

    private Component createEllipsesComponent() {
        return new JLabel(ELLIPSES, SwingConstants.CENTER);
    }

    private void addPageButtonRange(JPanel parentPanel, ButtonGroup buttonGroup, int start, int end) {
        for (; start <= end; start++) {
            addPageButton(parentPanel, buttonGroup, start);
        }
    }

    private void addPageButton(JPanel parentPanel, ButtonGroup buttonGroup, int pageNumber) {
        JRadioButton toggleButton = new JRadioButton(getDate(pageNumber));
        toggleButton.setName(String.valueOf(pageNumber));
        toggleButton.setBorderPainted(true);
        buttonGroup.add(toggleButton);
        parentPanel.add(toggleButton);
        if (pageNumber == currentPage) {
            toggleButton.setSelected(true);
        }
        toggleButton.addActionListener(ae -> {
            currentPage = Integer.parseInt(toggleButton.getName());
            refreshButton("-");
            search(pageNumber);
        });
    }

    private String getDate(int index) {
        String date = hmData.get(index);
        return Util1.toDateStr(date, "yyyy-MM-dd", "dd/MM/yy");
    }

    private void search(int index) {
        if (observer != null) {
            String date = hmData.get(index);
            observer.selected("Date-Search", date);
        }
    }
}
