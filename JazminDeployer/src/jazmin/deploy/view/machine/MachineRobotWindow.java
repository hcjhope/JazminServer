/**
 * 
 */
package jazmin.deploy.view.machine;

import java.io.IOException;

import jazmin.deploy.DeploySystemUI;
import jazmin.deploy.domain.JavaScriptSource;
import jazmin.deploy.manager.DeployManager;
import jazmin.deploy.ui.BeanTable;
import jazmin.deploy.view.main.InputWindow;

import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author yama
 * 6 Jan, 2015
 */
@SuppressWarnings("serial")
public class MachineRobotWindow extends Window{

	BeanTable<JavaScriptSource> table;
	AceEditor editor ;
	JavaScriptSource currentScript;
	//
	public MachineRobotWindow() {
		Responsive.makeResponsive(this);
		setCaption("Robots");
        setWidth(90.0f, Unit.PERCENTAGE);
        setHeight(90.0f, Unit.PERCENTAGE);
        center();
        setCloseShortcut(KeyCode.ESCAPE, null);
        setResizable(true);
        setClosable(true);
       
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        setContent(content);
        table = new BeanTable<JavaScriptSource>(null,JavaScriptSource.class);
		content.addComponent(table);
		table.setWidth("300px");
		table.setHeight("100%");
		//
        editor= new AceEditor();
		editor.setThemePath("/ace");
		editor.setModePath("/ace");
		editor.setWorkerPath("/ace");
		editor.setMode(AceMode.sh);
		editor.setShowPrintMargin(false);
		editor.setUseWorker(true);
		editor.setTheme(AceTheme.eclipse);
		editor.setWidth("100%");
		editor.setHeight("100%");
		HorizontalLayout topLayout=new HorizontalLayout(table,editor);
		topLayout.setExpandRatio(editor,1);
		topLayout.setSizeFull();
		content.addComponent(topLayout);
        content.setExpandRatio(topLayout, 1f);
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        //
        Label empty=new Label();
        footer.addComponent(empty);
        footer.setExpandRatio(empty, 1);
        //
        Button newBtn = new Button("New"); 
        newBtn.addStyleName(ValoTheme.BUTTON_SMALL);
        newBtn.addClickListener(e->newScript());
        footer.addComponent(newBtn);
        footer.setComponentAlignment(newBtn, Alignment.TOP_LEFT);
        //
        //
        Button deleteBtn = new Button("Delete"); 
        deleteBtn.addStyleName(ValoTheme.BUTTON_SMALL);
        deleteBtn.addClickListener(e->deleteScript());
        deleteBtn.addStyleName(ValoTheme.BUTTON_DANGER); 
        footer.addComponent(deleteBtn);
        footer.setComponentAlignment(deleteBtn, Alignment.TOP_LEFT);
        //
        Button save = new Button("Save");
        save.addStyleName(ValoTheme.BUTTON_SMALL);
        save.setClickShortcut(KeyCode.S,ShortcutAction.ModifierKey.META);
        save.addStyleName(ValoTheme.BUTTON_PRIMARY); 
        save.addClickListener(e->save());
        footer.addComponent(save);
        footer.setComponentAlignment(save, Alignment.TOP_RIGHT);
        //
        content.addComponent(footer);
        //
        table.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				JavaScriptSource script=table.getItemValue(event.getItem());
				showScriptContent(script);
			}
		});
        //
        loadData();
    }
	//
	private void showScriptContent(JavaScriptSource script){
		try {
			currentScript=script;
			editor.setValue(DeployManager.getRobotScriptContent(script.name));
		} catch(Exception e) {
			DeploySystemUI.showNotificationInfo("ERROR", e.getMessage());
		}
	}
	//
	private void loadData(){
		table.setBeanData(DeployManager.getRobotScripts());
	}
	//
	private void deleteScript(){
		JavaScriptSource script=table.getSelectValue();
		if(script==null){
			DeploySystemUI.showNotificationInfo("INFO", "Choose robot to delete");
			return;
		}
		DeployManager.deleteScript(script.name);
		loadData();
	}
	//
	private void newScript(){
		final InputWindow sw=new InputWindow(window->{
			String name=window.getInputValue();
			try {
				DeployManager.saveRobotScript(name,"");
				DeploySystemUI.showNotificationInfo("INFO", "Create complete");
				loadData();
				currentScript=DeployManager.getRobotScript(name);
				window.close();
			} catch (Exception e) {
				DeploySystemUI.showNotificationInfo("ERROR", e.getMessage());
			}
		});
		sw.setCaption("Create new robot");
		sw.setInfo("input robot name");
		UI.getCurrent().addWindow(sw);
	}
	//
	private void save(){
		if(currentScript==null){
			return;
		}
		try {
			DeployManager.saveRobotScript(currentScript.name,editor.getValue());
			DeploySystemUI.showNotificationInfo("INFO", "Save complete");
		} catch (IOException e) {
			DeploySystemUI.showNotificationInfo("ERROR", e.getMessage());
		}
	}
}
