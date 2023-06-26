package de.rpgframework.shadowrun6.updater;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.update4j.Archive;
import org.update4j.Configuration;
import org.update4j.FileMetadata;
import org.update4j.UpdateContext;
import org.update4j.UpdateOptions;
import org.update4j.UpdateOptions.ArchiveUpdateOptions;
import org.update4j.UpdateResult;
import org.update4j.service.DefaultLauncher;
import org.update4j.service.DefaultUpdateHandler;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * @author prelle
 *
 */
public class StartupView extends VBox {

	private final static Logger logger = System.getLogger("commlink6.updater");

	private ChoiceBox<ReleaseType> cbType;
	private ChoiceBox<Locale> cbLang;
	private ImageView iView, logos;

	private ProgressBar progFiles;
	private ProgressBar progPerFile;
	private Label lbState;

	private Button btnLaunch;
	private Button btnUpdate;

	private Configuration config;
	private Stage primaryStage;

	private transient String currentFile;

	//-------------------------------------------------------------------
	public StartupView(Stage stage) {
		this.primaryStage = stage;
		initComponents();
		initLayout();
		initInteractivity();
		cbType.setValue(ReleaseType.STABLE);
		Locale def = Locale.getDefault();
		if (def.getLanguage().equals("en")) cbLang.setValue(Locale.ENGLISH);
		if (def.getLanguage().equals("de")) cbLang.setValue(Locale.GERMAN);
		if (def.getLanguage().equals("fr")) cbLang.setValue(Locale.FRENCH);

		try {
			if (config.requiresUpdate()) {
				System.out.println("UPDATE REQUIRED");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//-------------------------------------------------------------------
	private void initComponents() {
		iView = new ImageView(new Image(ClassLoader.getSystemResourceAsStream("figure2.png")));
		logos = new ImageView(new Image(ClassLoader.getSystemResourceAsStream("Logos.png")));

		cbType = new ChoiceBox<>();
		cbType.getItems().addAll(ReleaseType.values());
		cbType.setConverter(new StringConverter<ReleaseType>() {
			public String toString(ReleaseType val) {
				if (val==ReleaseType.STABLE) return "Stable";
				if (val==ReleaseType.TESTING) return "Testing";
				if (val==ReleaseType.DEVELOP) return "Experimental";
				return (val!=null)?val.name():"?";
			}
			public ReleaseType fromString(String string) { return null;}
		});

		cbLang = new ChoiceBox<>();
		cbLang.getItems().addAll(Locale.ENGLISH, Locale.GERMAN, Locale.FRENCH, Locale.forLanguageTag("pr"));
		cbLang.setConverter(new StringConverter<Locale>() {
			public String toString(Locale val) {
				if (val==Locale.ENGLISH) return "English";
				if (val==Locale.GERMAN) return "Deutsch";
				if (val==Locale.FRENCH) return "Français";
				if (val==Locale.forLanguageTag("pr")) return "Português";
				return (val!=null)?val.getLanguage():"?";
			}
			public Locale fromString(String string) { return null;}
		});

		progFiles = new ProgressBar();
		progPerFile = new ProgressBar(0.0f);
		lbState   = new Label();
		lbState.setWrapText(true);

		btnLaunch = new Button("Launch application");
		btnUpdate = new Button("Update");
	}

	//-------------------------------------------------------------------
	private void initLayout() {
		setStyle("-fx-background-color: rgba(0,0,0,0);");

		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.setHgap(10);
		Label lbType = new Label("Type");
		Label lbLang = new Label("Language");
		lbType.setStyle("-fx-font-weight: bold");
		lbLang.setStyle("-fx-font-weight: bold");

		grid.add(lbType, 0, 0);
		grid.add(cbType, 1, 0);
		grid.add(lbLang, 0, 1);
		grid.add(cbLang, 1, 1);
		grid.add(progPerFile, 0, 2, 2,1);
		grid.add(lbState, 0, 4, 2,1);

		progPerFile.setMaxWidth(Double.MAX_VALUE);
		GridPane.setFillWidth(progPerFile, true);

		GridPane.setFillHeight(lbLang, true);
		GridPane.setHalignment(btnUpdate, HPos.CENTER);
		HBox.setMargin(iView, new Insets(-250, 0, 0, 0));
		GridPane.setHalignment(lbType, HPos.RIGHT);
		GridPane.setHalignment(lbLang, HPos.RIGHT);

		grid.getColumnConstraints().add(new ColumnConstraints(200));
		grid.getColumnConstraints().add(new ColumnConstraints(130));
		//getRowConstraints().add(new RowConstraints(250));

		Region buf2 = new Region();
		buf2.setPrefHeight(50);
		VBox gridPlusButtons = new VBox(20,grid,buf2,btnUpdate, btnLaunch);
		HBox.setMargin(gridPlusButtons, new Insets(4));
		gridPlusButtons.setAlignment(Pos.TOP_CENTER);

		HBox line = new HBox(20);
		line.getChildren().addAll(gridPlusButtons, iView);
		line.setStyle("-fx-background-color: white; -fx-background-radius: 30px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);");
		iView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);");
		logos.setStyle("-fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.8), 10, 0.5, 0.0, 0.0);");

		VBox.setMargin(logos, new Insets(114,0,0,20));
		getChildren().addAll(logos,line);
		HBox.setMargin(grid, new Insets(10,0,0,0));
		VBox.setMargin(line, new Insets(15));
	}

	//-------------------------------------------------------------------
	private void initInteractivity() {
		cbLang.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> Locale.setDefault(n));

		cbType.getSelectionModel().selectedItemProperty().addListener( (ov,o,n) -> {
			logger.log(Level.DEBUG, "Updated stability type to "+n);
			if (n==null) return;
			try {
				URL configUrl = new URL("http://"+n.getServer()+"/commlink6-updates/"+n.name().toLowerCase()+"/config.xml");
				logger.log(Level.INFO, "Search config at {0}", configUrl);
				config = null;
				try (Reader in = new InputStreamReader(configUrl.openStream(), StandardCharsets.UTF_8)) {
					config = Configuration.read(in);
				} catch (IOException e) {
					e.printStackTrace();
					Path local = Paths.get("../config.xml").toAbsolutePath();
					System.err.println("Could not load remote config, falling back to local: "+local);
					try (Reader in = Files.newBufferedReader(local)) {
						config = Configuration.read(in);
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				}

				logger.log(Level.INFO, "Config now "+config.getResolvedProperty("project.version"));
				if (config.requiresUpdate()) {
					btnUpdate.setText("Update to "+config.getResolvedProperty("project.version"));
					btnUpdate.setDisable(false);
					btnLaunch.setDisable(false);
				} else {
					btnUpdate.setDisable(true);
					btnLaunch.setDisable(false);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		btnUpdate.setOnAction(ev -> {
			update();
		});

		btnLaunch.setOnAction(ev -> {
			logger.log(Level.INFO, "Launcher "+config.getLauncher());
			logger.log(Level.INFO, "Launch "+config.getResolvedProperty(DefaultLauncher.MAIN_CLASS_PROPERTY_KEY));
			getScene().getWindow().hide();
			((Stage)getScene().getWindow()).close();
			System.setProperty("project.version", config.getResolvedProperty("project.version"));
			CommlinkLauncher.primaryStage = primaryStage;
			logger.log(Level.INFO, "Call config.launch()");
			config.launch();
//			Thread thread = new Thread( () -> {
//				try {
//					Thread.sleep(200);
//					Platform.exit();
//					Thread.sleep(2000);
//					config.launch();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			});
//			thread.start();
//			getScene().getWindow().hide();
		});
	}

	//-------------------------------------------------------------------
	public ReadOnlyObjectProperty<ReleaseType> releaseTypeProperty() {
		return cbType.getSelectionModel().selectedItemProperty();
	}

	private void update() {
		logger.log(Level.DEBUG,"Base = "+config.getBasePath()+" from "+config.getBaseUri());
		Path zip = Paths.get("/tmp/commlink_"+cbType.getValue().name().toLowerCase()+".zip");
		ArchiveUpdateOptions options = UpdateOptions.archive(zip);
		options.updateHandler(new DefaultUpdateHandler() {
		    @Override
		    public void startDownloads() throws Throwable {
		    	logger.log(Level.INFO, "startDownloads");
		    	Platform.runLater( () -> btnLaunch.setDisable(true));
		    }
		    @Override
		    public void doneDownloads() throws Throwable {
		    	logger.log(Level.INFO, "doneDownloads");
		    }
		    @Override
		    public void succeeded() {
		    	logger.log(Level.INFO, "succeeded");
		    	btnLaunch.setDisable(false);
		    }
		    @Override
		    public void updateDownloadFileProgress(FileMetadata file, float frac) throws Throwable {
		        super.updateDownloadFileProgress(file, frac);
		        currentFile = file.getPath().getFileName().toString();
		        Platform.runLater( () -> progPerFile.setProgress(frac));
		        //progPerFile.setProgress(frac);
		    }

		});
		Thread thread = new Thread( () -> {
			SSLFix.execute();
			UpdateResult result = config.update(options);
			logger.log(Level.ERROR, "Result "+result);
			UpdateContext obj = result.result();
			logger.log(Level.ERROR, "Result2 "+obj);
			if (result.getException()==null) {
				try {
					Archive.read(zip).install(true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				logger.log(Level.ERROR, "Update failed",result.getException());
				Platform.runLater( () -> lbState.setText("Update failed for "+currentFile))	;
			}
		});
		thread.start();
	}
}
