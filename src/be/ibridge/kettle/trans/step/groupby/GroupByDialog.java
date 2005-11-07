 /**********************************************************************
 **                                                                   **
 **               This code belongs to the KETTLE project.            **
 **                                                                   **
 ** It belongs to, is maintained by and is copyright 1999-2005 by     **
 **                                                                   **
 **      i-Bridge bvba                                                **
 **      Fonteinstraat 70                                             **
 **      9400 OKEGEM                                                  **
 **      Belgium                                                      **
 **      http://www.kettle.be                                         **
 **      info@kettle.be                                               **
 **                                                                   **
 **********************************************************************/
 
/*
 * Created on 2-jul-2003
 *
 */

//import java.text.DateFormat;
//import java.util.Date;

package be.ibridge.kettle.trans.step.groupby;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import be.ibridge.kettle.core.ColumnInfo;
import be.ibridge.kettle.core.Const;
import be.ibridge.kettle.core.Row;
import be.ibridge.kettle.core.dialog.ErrorDialog;
import be.ibridge.kettle.core.exception.KettleException;
import be.ibridge.kettle.core.value.Value;
import be.ibridge.kettle.core.widget.TableView;
import be.ibridge.kettle.trans.TransMeta;
import be.ibridge.kettle.trans.step.BaseStepDialog;
import be.ibridge.kettle.trans.step.BaseStepMeta;
import be.ibridge.kettle.trans.step.StepDialogInterface;


public class GroupByDialog extends BaseStepDialog implements StepDialogInterface
{
    public static final String STRING_SORT_WARNING_PARAMETER = "GroupSortWarning";
	private Label        wlGroup;
	private TableView    wGroup;
	private FormData     fdlGroup, fdGroup;

	private Label        wlAgg;
	private TableView    wAgg;
	private FormData     fdlAgg, fdAgg;

	private Label        wlAllRows;
	private Button       wAllRows;
	private FormData     fdlAlllRows, fdAllRows;

	private Label        wlFlagField;
	private Text         wFlagField;
	private FormData     fdlFlagField, fdFlagField;

	private Button wGet, wGetAgg;
	private FormData fdGet, fdGetAgg;
	private Listener lsGet, lsGetAgg;

	private GroupByMeta input;
	private boolean backup_all_rows;

	public GroupByDialog(Shell parent, Object in, TransMeta transMeta, String sname)
	{
		super(parent, (BaseStepMeta)in, transMeta, sname);
		input=(GroupByMeta)in;
	}

	public String open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		props.setLook(shell);

		ModifyListener lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				input.setChanged();
			}
		};
		backupChanged = input.hasChanged();
		backup_all_rows = input.passAllRows();

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText("Group By");
		
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;
		
		// Stepname line
		wlStepname=new Label(shell, SWT.RIGHT);
		wlStepname.setText("Step name ");
 		props.setLook(wlStepname);
		fdlStepname=new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right= new FormAttachment(middle, -margin);
		fdlStepname.top  = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
 		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname=new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top  = new FormAttachment(0, margin);
		fdStepname.right= new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);

		// Include all rows?
		wlAllRows=new Label(shell, SWT.RIGHT);
		wlAllRows.setText("Include all rows? ");
 		props.setLook(wlAllRows);
		fdlAlllRows=new FormData();
		fdlAlllRows.left = new FormAttachment(0, 0);
		fdlAlllRows.top  = new FormAttachment(wStepname, margin);
		fdlAlllRows.right= new FormAttachment(middle, -margin);
		wlAllRows.setLayoutData(fdlAlllRows);
		wAllRows=new Button(shell, SWT.CHECK );
 		props.setLook(wAllRows);
		fdAllRows=new FormData();
		fdAllRows.left = new FormAttachment(middle, 0);
		fdAllRows.top  = new FormAttachment(wStepname, margin);
		fdAllRows.right= new FormAttachment(100, 0);
		wAllRows.setLayoutData(fdAllRows);
		wAllRows.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent e) 
				{
					input.setPassAllRows( !input.passAllRows() );
					input.setChanged();
				}
			}
		);

		// Flag field size line
		wlFlagField=new Label(shell, SWT.RIGHT);
		wlFlagField.setText("Flag fieldname ");
 		props.setLook(wlFlagField);
		fdlFlagField=new FormData();
		fdlFlagField.left   = new FormAttachment(0, 0);
		fdlFlagField.right  = new FormAttachment(middle, -margin);
		fdlFlagField.top    = new FormAttachment(wAllRows, margin);
		wlFlagField.setLayoutData(fdlFlagField);
		wFlagField=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wFlagField);
		wFlagField.addModifyListener(lsMod);
		fdFlagField=new FormData();
		fdFlagField.left   = new FormAttachment(middle, 0);
		fdFlagField.right  = new FormAttachment(100, 0);
		fdFlagField.top    = new FormAttachment(wAllRows, margin);
		wFlagField.setLayoutData(fdFlagField);


		wlGroup=new Label(shell, SWT.NONE);
		wlGroup.setText("The fields that make up the group: ");
 		props.setLook(wlGroup);
		fdlGroup=new FormData();
		fdlGroup.left  = new FormAttachment(0, 0);
		fdlGroup.top   = new FormAttachment(wFlagField, margin);
		wlGroup.setLayoutData(fdlGroup);

		int nrKeyCols=1;
		int nrKeyRows=(input.getGroupField()!=null?input.getGroupField().length:1);
		
		ColumnInfo[] ciKey=new ColumnInfo[nrKeyCols];
		ciKey[0]=new ColumnInfo("Group field",  ColumnInfo.COLUMN_TYPE_TEXT,   "", false);
		
		wGroup=new TableView(shell, 
						      SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, 
						      ciKey, 
						      nrKeyRows,  
						      lsMod,
							  props
						      );

		wGet=new Button(shell, SWT.PUSH);
		wGet.setText(" &Get Fields ");
		fdGet = new FormData();
		fdGet.top   = new FormAttachment(wlGroup, margin);
		fdGet.right = new FormAttachment(100, 0);
		wGet.setLayoutData(fdGet);
		
		fdGroup=new FormData();
		fdGroup.left  = new FormAttachment(0, 0);
		fdGroup.top   = new FormAttachment(wlGroup, margin);
		fdGroup.right = new FormAttachment(wGet, -margin);
		fdGroup.bottom= new FormAttachment(45, 0);
		wGroup.setLayoutData(fdGroup);

		// THE Aggregate fields
		wlAgg=new Label(shell, SWT.NONE);
		wlAgg.setText("Aggregates :");
 		props.setLook(wlAgg);
		fdlAgg=new FormData();
		fdlAgg.left  = new FormAttachment(0, 0);
		fdlAgg.top   = new FormAttachment(wGroup, margin);
		wlAgg.setLayoutData(fdlAgg);
		
		int UpInsCols=3;
		int UpInsRows= (input.getAggregateField()!=null?input.getAggregateField().length:1);
		
		ColumnInfo[] ciReturn=new ColumnInfo[UpInsCols];
		ciReturn[0]=new ColumnInfo("Name",     ColumnInfo.COLUMN_TYPE_TEXT,   "", false);
		ciReturn[1]=new ColumnInfo("Subject",  ColumnInfo.COLUMN_TYPE_TEXT,   "", false);
		ciReturn[2]=new ColumnInfo("Type",     ColumnInfo.COLUMN_TYPE_CCOMBO, "", GroupByMeta.type_group_desc_long);
		
		wAgg=new TableView(shell, 
							  SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, 
							  ciReturn, 
							  UpInsRows,  
							  lsMod,
							  props
							  );

		wGetAgg=new Button(shell, SWT.PUSH);
		wGetAgg.setText(" &Get lookup fields ");
		fdGetAgg = new FormData();
		fdGetAgg.top   = new FormAttachment(wlAgg, margin);
		fdGetAgg.right = new FormAttachment(100, 0);
		wGetAgg.setLayoutData(fdGetAgg);

		// THE BUTTONS
		wOK=new Button(shell, SWT.PUSH);
		wOK.setText(" &OK ");
		wCancel=new Button(shell, SWT.PUSH);
		wCancel.setText(" &Cancel ");

		setButtonPositions(new Button[] { wOK, wCancel }, margin, null);

		fdAgg=new FormData();
		fdAgg.left  = new FormAttachment(0, 0);
		fdAgg.top   = new FormAttachment(wlAgg, margin);
		fdAgg.right = new FormAttachment(wGetAgg, -margin);
		fdAgg.bottom= new FormAttachment(wOK, -margin);
		wAgg.setLayoutData(fdAgg);

		// Add listeners
		lsOK       = new Listener() { public void handleEvent(Event e) { ok();        } };
		lsGet      = new Listener() { public void handleEvent(Event e) { get();       } };
		lsGetAgg   = new Listener() { public void handleEvent(Event e) { getAgg(); } };
		lsCancel   = new Listener() { public void handleEvent(Event e) { cancel();    } };
		
		wOK.addListener    (SWT.Selection, lsOK    );
		wGet.addListener   (SWT.Selection, lsGet   );
		wGetAgg.addListener (SWT.Selection, lsGetAgg );
		wCancel.addListener(SWT.Selection, lsCancel);
		
		lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		
		wStepname.addSelectionListener( lsDef );
		
		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );



		// Set the shell size, based upon previous time...
		setSize();
				
		getData();
		input.setChanged(backupChanged);

		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
		return stepname;
	}

	/**
	 * Copy information from the meta-data input to the dialog fields.
	 */ 
	public void getData()
	{
		int i;
		log.logDebug(toString(), "getting key info...");
		
		wAllRows.setSelection(input.passAllRows());
		if (input.getPassFlagField()!=null) wFlagField.setText(input.getPassFlagField());
		
		if (input.getGroupField()!=null)
		for (i=0;i<input.getGroupField().length;i++)
		{
			TableItem item = wGroup.table.getItem(i);
			if (input.getGroupField()[i]   !=null) item.setText(1, input.getGroupField()[i]);
		}
		
		if (input.getAggregateField()!=null)
		for (i=0;i<input.getAggregateField().length;i++)
		{
			TableItem item = wAgg.table.getItem(i);
			if (input.getAggregateField()[i]!=null     ) item.setText(1, input.getAggregateField()[i]);
			if (input.getSubjectField()[i]!=null       ) item.setText(2, input.getSubjectField()[i]);
			item.setText(3, GroupByMeta.getTypeDescLong(input.getAggregateType()[i]));
		}
		
		wStepname.selectAll();
		wGroup.setRowNums();
		wGroup.optWidth(true);
		wAgg.setRowNums();
		wAgg.optWidth(true);
	}
	
	private void cancel()
	{
		stepname=null;
		input.setChanged(backupChanged);
		input.setPassAllRows(  backup_all_rows );
		dispose();
	}
	
	private void ok()
	{
		input.setPassFlagField( wFlagField.getText() );
		
		//ignore_aggregate 
		//field_ignore    
		
		int sizegroup = wGroup.nrNonEmpty();
		int nrfields = wAgg.nrNonEmpty();
		
		input.allocate(sizegroup, nrfields);
				
		for (int i=0;i<sizegroup;i++)
		{
			TableItem item = wGroup.getNonEmpty(i);
			input.getGroupField()[i]    = item.getText(1);
		}
		
		for (int i=0;i<nrfields;i++)
		{
			TableItem item      = wAgg.getNonEmpty(i);
			input.getAggregateField()[i]  = item.getText(1);		
			input.getSubjectField()[i]    = item.getText(2);		
			input.getAggregateType()[i]       = GroupByMeta.getType(item.getText(3));		
		}
		
		stepname = wStepname.getText();
        
        if ( "Y".equalsIgnoreCase( props.getCustomParameter(STRING_SORT_WARNING_PARAMETER, "Y") ))
        {
            MessageDialogWithToggle md = new MessageDialogWithToggle(shell, 
                 "Warning!", 
                 null,
                 "The 'group by' function needs the input to be sorted on the specified keys." + Const.CR + "If you don't sort the input, the results may not be correct!"+Const.CR,
                 MessageDialog.WARNING,
                 new String[] { "I understand" },
                 0,
                 "Please, don't show this warning anymore.",
                 "N".equalsIgnoreCase( props.getCustomParameter(STRING_SORT_WARNING_PARAMETER, "Y") )
            );
            md.open();
            props.setCustomParameter(STRING_SORT_WARNING_PARAMETER, md.getToggleState()?"N":"Y");
            props.saveProps();
        }
					
		dispose();
	}

	private void get()
	{
		try
		{
			Row r = transMeta.getPrevStepFields(stepname);
			if (r!=null)
			{
				Table table=wGroup.table;
				for (int i=0;i<r.size();i++)
				{
					Value v = r.getValue(i);
					TableItem ti = new TableItem(table, SWT.NONE);
					ti.setText(1, v.getName());
				}
				wGroup.removeEmptyRows();
				wGroup.setRowNums();
				wGroup.optWidth(true);
			}
		}
		catch(KettleException ke)
		{
			new ErrorDialog(shell, props, "Get fields failed", "Unable to get fields from previous steps because of an error", ke);
		}
	}

	private void getAgg()
	{
		try
		{
			Row r = transMeta.getPrevStepFields(stepname);
			if (r!=null)
			{
				Table table=wAgg.table;
				for (int i=0;i<r.size();i++)
				{
					Value v = r.getValue(i);
					TableItem ti = new TableItem(table, SWT.NONE);
					ti.setText(1, v.getName());
					ti.setText(2, v.getName());
					ti.setText(3, "");
				}
				wAgg.removeEmptyRows();
				wAgg.setRowNums();
				wAgg.optWidth(true);
			}
		}
		catch(KettleException ke)
		{
			new ErrorDialog(shell, props, "Get fields failed", "Unable to get fields from previous steps because of an error", ke);
		}
	}
	
	public String toString()
	{
		return this.getClass().getName();
	}
}
