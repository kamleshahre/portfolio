package name.abuchen.portfolio.ui.views;

import java.util.List;

import org.eclipse.swt.widgets.ToolBar;

import name.abuchen.portfolio.snapshot.ReportingPeriod;
import name.abuchen.portfolio.ui.AbstractFinanceView;
import name.abuchen.portfolio.ui.util.ReportingPeriodDropDown;
import name.abuchen.portfolio.ui.util.ReportingPeriodDropDown.ReportingPeriodListener;

public abstract class AbstractHistoricView extends AbstractFinanceView implements ReportingPeriodListener
{
    private ReportingPeriodDropDown dropDown;

    @Override
    protected void addButtons(final ToolBar toolBar)
    {
        dropDown = new ReportingPeriodDropDown(toolBar, getPart(), this);
    }

    protected final ReportingPeriod getReportingPeriod()
    {
        return dropDown.getPeriods().getFirst();
    }

    protected final List<ReportingPeriod> getReportingPeriods()
    {
        return dropDown.getPeriods();
    }
}
