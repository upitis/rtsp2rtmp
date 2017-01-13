package ru.spb.vksc;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.ValoTheme;
import ru.spb.vksc.backend.FfmpegProcesses;
import ru.spb.vksc.backend.Rtsp2RtmpProcess;

import java.util.ArrayList;
import java.util.List;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@Widgetset("ru.spb.vksc.MyAppWidgetset")
@Title("rtsp2rtmp")
public class MyUI extends UI {
    private Rtsp2RtmpProcess process;
    private List<HorizontalLayout> processLayouts = new ArrayList<>();
    private List<Label> processLabels = new ArrayList<>();
    private List<Button> killButtons = new ArrayList<>();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout vLayout = new VerticalLayout();
        final HorizontalLayout hLayout = new HorizontalLayout();

        final TextField rtspUrl = new TextField();
        rtspUrl.setInputPrompt("rtsp URL");
        rtspUrl.setWidth("400");

        final NativeButton nativeButton = new NativeButton("-->");
        nativeButton.setEnabled(false);

        final TextField rtmpUrl = new TextField();
        rtmpUrl.setInputPrompt("rtmp URL");
        rtmpUrl.setWidth("400");

        Button button = new Button();
        button.setIcon(FontAwesome.PLAY_CIRCLE);
        button.addClickListener( e -> {
            process = new Rtsp2RtmpProcess(rtspUrl.getValue(), rtmpUrl.getValue());
        });

        hLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        hLayout.addComponents(rtspUrl, nativeButton, rtmpUrl, button);

        Label rtspUrlExample = new Label("Example for Axis Camera - rtsp://root:TANDBERG@192.168.110.204/axis-media/media.amp");
        Label rtmpUrlExample = new Label("Example for vks.vpn Live RTMP - rtmp://10.128.2.102:1935/live/vksstream");
        rtspUrlExample.setStyleName(ValoTheme.LABEL_TINY);
        rtmpUrlExample.setStyleName(ValoTheme.LABEL_TINY);

        vLayout.setWidth("100%");
        vLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        vLayout.addComponents(hLayout, rtspUrlExample, rtmpUrlExample);
        vLayout.setMargin(true);
        vLayout.setSpacing(true);

        JavaScript.getCurrent().execute("setInterval(function(){refreshOutput();}, 2000);");
        JavaScript.getCurrent().addFunction("refreshOutput", jsonArray -> {
            for (int i = 0; i < processLayouts.size(); i++) {
                processLayouts.get(i).removeComponent(processLabels.get(i));
                processLayouts.get(i).removeComponent(killButtons.get(i));
                vLayout.removeComponent(processLayouts.get(i));
            }
            processLayouts.clear();
            processLabels.clear();
            killButtons.clear();

            for (String p : FfmpegProcesses.getFfmepgProcesses()) {
                HorizontalLayout horizontalLayout = new HorizontalLayout();
                processLayouts.add(horizontalLayout);

                Label label = new Label(p.trim(), ContentMode.HTML);
                label.setStyleName(ValoTheme.LABEL_SUCCESS);
                processLabels.add(label);

                Button killButton = new Button("kill");
                killButton.setIcon(FontAwesome.CLOSE);
                killButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
                killButton.addClickListener((Button.ClickListener) clickEvent -> {

                    String[] ss = p.trim().split(" ");
                    if (ss.length > 0) {
                        FfmpegProcesses.killProcess(ss[0]);
                    }
                });
                killButtons.add(killButton);

                horizontalLayout.addComponents(label, killButton);
                vLayout.addComponent(horizontalLayout);
            }
        });
        
        setContent(vLayout);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
