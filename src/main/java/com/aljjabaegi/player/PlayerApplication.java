package com.aljjabaegi.player;

import com.aljjabaegi.player.component.*;
import com.aljjabaegi.player.component.popup.About;
import com.aljjabaegi.player.component.popup.MultiMediaPlayer;
import com.aljjabaegi.player.component.popup.Settings;
import com.aljjabaegi.player.component.popup.Shortcut;
import com.aljjabaegi.player.config.MessageConfig;
import com.aljjabaegi.player.service.cctv.CctvService;
import com.aljjabaegi.player.service.cctv.record.CctvDto;
import com.aljjabaegi.player.service.code.CodeService;
import com.aljjabaegi.player.service.code.record.CodeDto;
import com.aljjabaegi.player.util.CommonVariable;
import com.aljjabaegi.player.util.Utils;
import com.aljjabaegi.player.util.setting.SettingUtil;
import com.formdev.flatlaf.FlatDarculaLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.*;

/**
 * RTSP protocol streaming player
 * 제어 : CCTV(01), 교차로 감시(03)
 * 영상 출력 : CCTV(01), 교차로 감시(03), VMS(04), 스마트 교차로(05)
 *
 * @author GEONLEE
 * @since 2024-01-02<br />
 * 2024-04-16 GEONLEE - Refactoring
 */
@SpringBootApplication
public class PlayerApplication extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerApplication.class);
    private final MessageConfig messageConfig;
    private final CctvService cctvService;
    private final CodeService codeService;
    public Settings settings;
    public Shortcut shortcut;
    public About about;
    public CustomMediaPlayer mediaPlayer;
    public ArrayList<CctvDto> normalCctvList;
    @Value("${project.name}")
    public String projectName;
    @Value("${project.version.application}")
    public String projectVersion;
    @Value("${project.version.java}")
    public String javaVersion;
    @Value("${project.version.springboot}")
    public String springbootVersion;
    public MultiMediaPlayer multiMediaPlayer;
    private LoadingBar loadingBar;
    private CustomTable cctvTable;
    private ControlPanel controlPanel;

    public PlayerApplication(MessageConfig messageConfig, CctvService cctvService, CodeService codeService) {
        FlatDarculaLaf.setup(); /*UI 적용*/
        SettingUtil.checkSettingFiles();
        setTitle("CCTV Controller");
        this.cctvService = cctvService;
        this.codeService = codeService;
        this.messageConfig = messageConfig;
        SwingUtilities.invokeLater(this::initGUI);
    }

    public static void main(String[] args) {
        SpringApplication.run(PlayerApplication.class, args);
    }

    /**
     * table option setting 메서드
     */
    private HashMap<CustomTableOptions, Object> getTableOption() {
        HashMap<CustomTableOptions, Object> tableOption = new HashMap<>();
        /*List.of, Arrays.asList 는 가변*/
        ArrayList<String> columns = new ArrayList<>(Arrays.asList("상태", "CCTV 유형", "CCTV 명", "CCTV IP", "타입 코드", "Streaming URL"));
        ArrayList<Integer> columnSize = new ArrayList<>(Arrays.asList(50, 85, 200, 130, 0, 0));
        tableOption.put(CustomTableOptions.COLUMNS, columns);
        tableOption.put(CustomTableOptions.COLUMN_SIZE, columnSize);
        tableOption.put(CustomTableOptions.SORTABLE, true);
        tableOption.put(CustomTableOptions.HEADER_CHECKBOX, true);
        return tableOption;
    }

    /**
     * GUI 생성 메서드
     */
    private void initGUI() {
        EventQueue.invokeLater(() -> {
            getContentPane().setLayout(new BorderLayout());
            setUndecorated(true); /*hidden default title*/
            setSize(1330, 610); /*setting main frame size*/
            setIconImage(Objects.requireNonNull(Utils.getImage("image/geonmoticon-96x96.png")).getImage()); /*최소화 아이콘*/
            CustomTitleBar customTitleBar = new CustomTitleBar(this);
            CustomMenu menuBar = new CustomMenu(this);
            customTitleBar.addMenu(menuBar);
            add(customTitleBar, BorderLayout.NORTH);

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            this.loadingBar = new LoadingBar(this);
            this.mediaPlayer = new CustomMediaPlayer(this, splitPane);
            // add popup frame
            // get cctv type
            List<CodeDto> codeList = codeService.getCodeList();
            SettingUtil.setCctvTypeMap(codeList);
            this.settings = new Settings(this);
            this.shortcut = new Shortcut(this);
            this.about = new About(this);
            // add grid frame
            this.cctvTable = new CustomTable(getTableOption());
            this.normalCctvList = new ArrayList<>();

            /*table 행 클릭 이벤트 추가*/
            this.cctvTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Map<String, String> data = cctvTable.getSelectedRowData();
                    if (data != null) {
                        String ip = data.get("CCTV IP");
                        String selectedIp = controlPanel.getSelectedIp();
                        if (mediaPlayer.isPlaying() && selectedIp != null && selectedIp.equals(ip)) {
                            return;
                        }
                        initVideoComponent();
                        String type = data.get("타입 코드"), status = data.get("상태");
                        if ("정상".equals(status) && setSelectedIp(ip, type)) {
                            controlPanel.play();
                        } else {
                            Utils.showAlertDialog(getContentPane(), "경고",
                                    messageConfig.getMsg("ABNORMAL.STATUS.MSG"), JOptionPane.ERROR_MESSAGE, null);
                        }
                    }
                }
            });
            /*CCTV 상태에 따라 텍스트 색상 변경*/
            this.cctvTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    Border border = BorderFactory.createMatteBorder(1, 0, 1, 1, Color.GRAY);
                    ((JComponent) component).setBorder(BorderFactory.createCompoundBorder(
                            border, // Border 설정
                            new EmptyBorder(0, 5, 0, 0) // Padding 설정
                    ));
                    if ("이상".equals(value)) {
                        component.setForeground(Color.RED);
                    } else {
                        component.setForeground(Color.GREEN);
                    }
                    setHorizontalAlignment(SwingConstants.CENTER);
                    return component;
                }
            });
            /*그리드 및 영상 화면 사이즈 설정*/
            splitPane.setDividerSize(0);
            splitPane.setLeftComponent(new JScrollPane(this.cctvTable));
            /*제어 패널 설정*/
            this.controlPanel = new ControlPanel(this, this.mediaPlayer, splitPane);
            /*기본 layout 설정*/
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    mediaPlayer.release();
                    System.exit(0);
                }
            });
            getContentPane().add(splitPane, BorderLayout.CENTER);
            getContentPane().add(this.controlPanel, BorderLayout.SOUTH);
            setResizable(false);
            setLocationRelativeTo(null);
            setVisible(true);
            requestFocus();
        });
    }

    /**
     * popup, input dialog 출력 메서드 rtsp 주소를 받아 영상을 출력 한다.
     */
    public void showOpenRtspDialog() {
        this.cctvTable.clearSelection();
        this.controlPanel.getButton("PLAY").setEnabled(false);
        String streamingAddr = JOptionPane.showInputDialog(getContentPane(), messageConfig.getMsg("STREAMING.INPUT.MSG"));
        if (streamingAddr != null) {
            initVideoComponent();
            this.mediaPlayer.play(streamingAddr);
        }
    }

    /**
     * multi media player popup 표출 메서드<br />
     * 이미 생성된 MultiMediaPlayer 일 경우 정상 cctv combobox 데이터 리로드 기능 추가
     */
    public void showMultiMediaPlayer() {
        if (this.multiMediaPlayer == null) {
            this.multiMediaPlayer = new MultiMediaPlayer();
        }
        this.multiMediaPlayer.setVisible(true);
        this.multiMediaPlayer.reloadNormalCctvCombos();
    }

    /**
     * CCTV table 정보 갱신 메서드
     */
    public void reloadTable() {
        Thread reload = new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                this.loadingBar.showLoadingBar(true);
            });
            try {
                this.cctvTable.clearRows();
                this.normalCctvList.clear();
                initVideoComponent();
                Thread.sleep(1000);
                List<CctvDto> list = this.cctvService.getCctvList();
                if (list == null) {
                    Utils.showAlertDialog(getContentPane(), "경고",
                            messageConfig.getMsg("DB.CONNECT.FAIL.MSG"), JOptionPane.ERROR_MESSAGE, null);
                    return;
                }
                this.loadingBar.setMaxLoading(list.size());
                SwingUtilities.invokeLater(() -> {
                    this.loadingBar.showLoadingBar(true);
                });
                for (int i = 0, n = list.size(); i < n; i++) {
                    CctvDto dto = list.get(i);
                    String status = "이상";
                    String ip = dto.ip();
                    String type = dto.cctvType();
                    boolean result = false;
                    if (ip != null && !"".equals(ip) && CommonVariable.ipPattern.matcher(ip).matches()) {
                        result = this.controlPanel.getControlService().getStatus(ip, type);
                    }
                    if (result) {
                        status = "정상";
                        this.normalCctvList.add(dto);
                    }
                    this.cctvTable.addRow(new Object[]{status, dto.cctvTypeName(), dto.cctvName() + "(" + dto.cctvId() + ")",
                            ip, dto.cctvType(), this.controlPanel.getControlService().getStreamingAddr(ip, type)});
                    this.loadingBar.setLoadingText(i);
                }
                this.cctvTable.sort(0, SortOrder.DESCENDING);
                Utils.showAlertDialog(getContentPane(), "확인",
                        messageConfig.getMsg("RELOAD.CCTV.SUCCESS.MSG"), JOptionPane.INFORMATION_MESSAGE, null);
            } catch (InterruptedException | RuntimeException e) {
                LOGGER.error(messageConfig.getMsg("RELOAD.CCTV.FAIL.MSG"), e);
                Utils.showAlertDialog(getContentPane(), "경고",
                        messageConfig.getMsg("RELOAD.CCTV.FAIL.MSG"), JOptionPane.ERROR_MESSAGE, null);
                this.cctvTable.clearRows();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    this.loadingBar.showLoadingBar(false);
                });
            }
        });
        reload.start();
    }

    /**
     * 영상 표출 화면 초기화 메서드
     */
    private void initVideoComponent() {
        setSelectedIp(null, null);
        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
        }
        this.mediaPlayer.reset();
        this.controlPanel.getButton("PLAY").setEnabled(false);
        this.controlPanel.controlMode(false);
    }

    /**
     * CCTV 영상 출력 및 제어할 ip setter
     */
    private boolean setSelectedIp(String ip, String type) {
        if (ip != null && !"".equals(ip) && CommonVariable.ipPattern.matcher(ip).matches()) {
            this.controlPanel.setSelectedIp(ip);
            this.controlPanel.setSelectedType(type);
            return true;
        } else {
            this.controlPanel.setSelectedIp(null);
            this.controlPanel.setSelectedType(null);
            return false;
        }
    }
}