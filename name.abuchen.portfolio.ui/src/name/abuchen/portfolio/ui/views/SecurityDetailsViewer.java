package name.abuchen.portfolio.ui.views;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import name.abuchen.portfolio.model.Classification;
import name.abuchen.portfolio.model.Classification.Assignment;
import name.abuchen.portfolio.model.Client;
import name.abuchen.portfolio.model.LatestSecurityPrice;
import name.abuchen.portfolio.model.Security;
import name.abuchen.portfolio.model.Taxonomy;
import name.abuchen.portfolio.model.Taxonomy.Visitor;
import name.abuchen.portfolio.model.Values;
import name.abuchen.portfolio.ui.Messages;
import name.abuchen.portfolio.ui.PortfolioPlugin;
import name.abuchen.portfolio.ui.util.Colors;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class SecurityDetailsViewer
{
    private static final String EMPTY_LABEL = ""; //$NON-NLS-1$

    public enum Facet
    {
        MASTER_DATA(MasterDataFacet.class), //
        LATEST_QUOTE(LatestQuoteFacet.class);

        private Class<? extends SecurityFacet> clazz;

        private Facet(Class<? extends SecurityFacet> clazz)
        {
            this.clazz = clazz;
        }

        public SecurityFacet create(Font font, Color color) throws InstantiationException, IllegalAccessException,
                        InvocationTargetException, NoSuchMethodException
        {
            return clazz.getConstructor(Font.class, Color.class).newInstance(font, color);
        }
    }

    private abstract static class SecurityFacet
    {
        private Font boldFont;
        private Color color;

        SecurityFacet(Font boldFont, Color color)
        {
            this.boldFont = boldFont;
            this.color = color;
        }

        abstract Control createViewControl(Composite parent, Client client);

        abstract void setInput(Security security);

        protected Label createHeading(Composite parent, String text)
        {
            Label heading = new Label(parent, SWT.NONE);
            heading.setText(text);
            heading.setFont(boldFont);
            heading.setForeground(color);
            return heading;
        }

        protected void below(Label referenceItem, Label value)
        {
            FormData data;
            data = new FormData();
            data.top = new FormAttachment(referenceItem, 5);
            data.left = new FormAttachment(referenceItem, 0, SWT.LEFT);
            data.right = new FormAttachment(100);
            value.setLayoutData(data);
        }

    }

    private static class MasterDataFacet extends SecurityFacet
    {
        private Label valueName;
        private Label valueISIN;
        private Label valueTickerSymbol;

        public MasterDataFacet(Font boldFont, Color color)
        {
            super(boldFont, color);
        }

        @Override
        Control createViewControl(Composite parent, Client client)
        {
            Composite composite = new Composite(parent, SWT.NONE);

            Label heading = createHeading(composite, Messages.ClientEditorLabelClientMasterData);

            valueName = new Label(composite, SWT.NONE);
            valueISIN = new Label(composite, SWT.NONE);
            valueTickerSymbol = new Label(composite, SWT.NONE);

            // layout

            FormLayout layout = new FormLayout();
            layout.marginLeft = 5;
            layout.marginRight = 5;
            composite.setLayout(layout);

            FormData data = new FormData();
            data.top = new FormAttachment(0, 5);
            heading.setLayoutData(data);

            data = new FormData();
            data.top = new FormAttachment(heading, 5);
            data.left = new FormAttachment(0);
            data.right = new FormAttachment(100);
            valueName.setLayoutData(data);

            below(valueName, valueISIN);
            below(valueISIN, valueTickerSymbol);

            return composite;
        }

        @Override
        void setInput(Security security)
        {
            if (security == null)
            {
                valueName.setText(EMPTY_LABEL);
                valueISIN.setText(EMPTY_LABEL);
                valueTickerSymbol.setText(EMPTY_LABEL);
            }
            else
            {
                valueName.setText(security.getName());
                valueISIN.setText(security.getIsin());
                valueTickerSymbol.setText(security.getTickerSymbol());
            }
        }
    }

    private static class LatestQuoteFacet extends SecurityFacet
    {
        private Label valueLatestPrices;
        private Label valueLatestTrade;
        private Label valueDaysHigh;
        private Label valueDaysLow;
        private Label valueVolume;
        private Label valuePreviousClose;

        public LatestQuoteFacet(Font boldFont, Color color)
        {
            super(boldFont, color);
        }

        @Override
        public Control createViewControl(Composite parent, Client client)
        {
            Composite composite = new Composite(parent, SWT.NONE);

            Label headingQuotes = createHeading(composite, Messages.ColumnLatestPrice);

            Label labelLatestPrice = new Label(composite, SWT.NONE);
            labelLatestPrice.setText(Messages.ColumnLatestPrice);
            valueLatestPrices = new Label(composite, SWT.RIGHT);

            Label labelLatestTrade = new Label(composite, SWT.NONE);
            labelLatestTrade.setText(Messages.ColumnLatestTrade);
            valueLatestTrade = new Label(composite, SWT.RIGHT);

            Label labelDaysHigh = new Label(composite, SWT.NONE);
            labelDaysHigh.setText(Messages.ColumnDaysHigh);
            valueDaysHigh = new Label(composite, SWT.RIGHT);

            Label labelDaysLow = new Label(composite, SWT.NONE);
            labelDaysLow.setText(Messages.ColumnDaysLow);
            valueDaysLow = new Label(composite, SWT.RIGHT);

            Label labelVolume = new Label(composite, SWT.NONE);
            labelVolume.setText(Messages.ColumnVolume);
            valueVolume = new Label(composite, SWT.RIGHT);

            Label labelPreviousClose = new Label(composite, SWT.NONE);
            labelPreviousClose.setText(Messages.ColumnPreviousClose);
            valuePreviousClose = new Label(composite, SWT.RIGHT);

            // layout

            FormLayout layout = new FormLayout();
            layout.marginLeft = 5;
            layout.marginRight = 5;
            composite.setLayout(layout);

            FormData data = new FormData();
            data.top = new FormAttachment(0, 5);
            headingQuotes.setLayoutData(data);

            data = new FormData();
            data.top = new FormAttachment(valueLatestPrices, 0, SWT.CENTER);
            labelLatestPrice.setLayoutData(data);

            data = new FormData();
            data.top = new FormAttachment(headingQuotes, 5);
            data.left = new FormAttachment(50, 5);
            data.right = new FormAttachment(100);
            valueLatestPrices.setLayoutData(data);

            GC gc = new GC(composite);
            Point extentText = gc.stringExtent("YYYY-MM-DD"); //$NON-NLS-1$
            gc.dispose();
            below(valueLatestPrices, labelLatestTrade, valueLatestTrade, extentText.x);

            below(valueLatestTrade, labelDaysHigh, valueDaysHigh, SWT.DEFAULT);
            below(valueDaysHigh, labelDaysLow, valueDaysLow, SWT.DEFAULT);
            below(valueDaysLow, labelVolume, valueVolume, SWT.DEFAULT);
            below(valueVolume, labelPreviousClose, valuePreviousClose, SWT.DEFAULT);

            return composite;
        }

        protected void below(Label referenceItem, Label label, Label value, int width)
        {
            FormData data;
            data = new FormData();
            data.top = new FormAttachment(value, 0, SWT.CENTER);
            label.setLayoutData(data);

            data = new FormData();
            data.top = new FormAttachment(referenceItem, 5);
            data.left = new FormAttachment(referenceItem, 0, SWT.LEFT);
            data.right = new FormAttachment(100);
            data.width = width;
            value.setLayoutData(data);
        }

        @Override
        public void setInput(Security security)
        {
            if (security == null || security.getLatest() == null)
            {
                valueLatestPrices.setText(EMPTY_LABEL);
                valueLatestTrade.setText(EMPTY_LABEL);
                valueDaysHigh.setText(EMPTY_LABEL);
                valueDaysLow.setText(EMPTY_LABEL);
                valueVolume.setText(EMPTY_LABEL);
                valuePreviousClose.setText(EMPTY_LABEL);
            }
            else
            {
                LatestSecurityPrice p = security.getLatest();

                valueLatestPrices.setText(Values.Amount.format(p.getValue()));
                valueLatestTrade.setText(Values.Date.format(p.getTime()));
                long daysHigh = p.getHigh();
                valueDaysHigh.setText(daysHigh == -1 ? Messages.LabelNotAvailable : Values.Amount.format(daysHigh));
                long daysLow = p.getLow();
                valueDaysLow.setText(daysLow == -1 ? Messages.LabelNotAvailable : Values.Amount.format(daysLow));
                long volume = p.getVolume();
                valueVolume.setText(volume == -1 ? Messages.LabelNotAvailable : String.format("%,d", volume)); //$NON-NLS-1$
                long prevClose = p.getPreviousClose();
                valuePreviousClose.setText(prevClose == -1 ? Messages.LabelNotAvailable : Values.Amount
                                .format(prevClose));
            }
        }

    }

    private static class TaxonomyFacet extends SecurityFacet
    {
        private Taxonomy taxonomy;

        private Label heading;
        private List<Label> labels = new ArrayList<Label>();

        public TaxonomyFacet(Taxonomy taxonomy, Font boldFont, Color color)
        {
            super(boldFont, color);
            this.taxonomy = taxonomy;
        }

        @Override
        Control createViewControl(Composite parent, Client client)
        {
            Composite composite = new Composite(parent, SWT.NONE);
            FormLayout layout = new FormLayout();
            layout.marginLeft = 5;
            layout.marginRight = 5;
            composite.setLayout(layout);

            heading = createHeading(composite, taxonomy.getName());
            FormData data = new FormData();
            data.top = new FormAttachment(0, 5);
            data.left = new FormAttachment(0);
            data.right = new FormAttachment(100);
            heading.setLayoutData(data);

            for (int ii = 0; ii < taxonomy.getHeigth() - 1; ii++)
            {
                Label label = new Label(composite, SWT.NONE);
                labels.add(label);

                if (ii == 0)
                {
                    data = new FormData();
                    data.top = new FormAttachment(heading, 5);
                    data.left = new FormAttachment(0);
                    data.right = new FormAttachment(100);
                    label.setLayoutData(data);
                }
                else
                {
                    below(labels.get(ii - 1), label);
                }
            }

            return composite;
        }

        @Override
        void setInput(final Security security)
        {
            final int[] count = new int[1];
            final int[] weight = new int[1];
            final Classification[] classification = new Classification[1];

            if (security != null)
            {
                taxonomy.foreach(new Visitor()
                {
                    @Override
                    public void visit(Classification thisClassification, Assignment assignment)
                    {
                        if (security.equals(assignment.getInvestmentVehicle()))
                        {
                            count[0]++;
                            if (assignment.getWeight() > weight[0])
                            {
                                weight[0] = assignment.getWeight();
                                classification[0] = thisClassification;
                            }
                        }
                    }
                });
            }

            if (count[0] > 1)
                heading.setText(taxonomy.getName() + ' ' + MessageFormat.format("(1 of {0})", count[0]));
            else
                heading.setText(taxonomy.getName());

            List<Classification> path = classification[0] != null ? classification[0].getPathToRoot()
                            : new ArrayList<Classification>();
            for (int ii = 0; ii < labels.size(); ii++)
                labels.get(ii).setText(path.size() > ii + 1 ? escape(path.get(ii + 1).getName()) : EMPTY_LABEL);
        }

        private String escape(String label)
        {
            return label.replaceAll("&", "&&"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private Composite container;

    private List<SecurityFacet> children = new ArrayList<SecurityFacet>();

    public SecurityDetailsViewer(Composite parent, int style, Client client)
    {
        this(parent, style, client, false);
    }

    public SecurityDetailsViewer(Composite parent, int style, Client client, boolean showMasterData)
    {
        container = new Composite(parent, style);
        container.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        container.setBackgroundMode(SWT.INHERIT_FORCE);

        // fonts

        LocalResourceManager resources = new LocalResourceManager(JFaceResources.getResources(), container);
        Font boldFont = resources.createFont(FontDescriptor.createFrom(container.getFont()).setStyle(SWT.BOLD));
        Color color = resources.createColor(Colors.HEADINGS.swt());

        // facets

        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(container);

        if (showMasterData)
            children.add(new MasterDataFacet(boldFont, color));

        children.add(new LatestQuoteFacet(boldFont, color));

        for (Taxonomy taxonomy : client.getTaxonomies())
            children.add(new TaxonomyFacet(taxonomy, boldFont, color));

        for (SecurityFacet child : children)
        {
            try
            {
                Control control = child.createViewControl(container, client);
                GridDataFactory.fillDefaults().grab(true, false).applyTo(control);
            }
            catch (Exception e)
            {
                PortfolioPlugin.log(e);
            }
        }
    }

    public Control getControl()
    {
        return container;
    }

    public void setInput(Security security)
    {
        for (SecurityFacet child : children)
            child.setInput(security);
    }
}
