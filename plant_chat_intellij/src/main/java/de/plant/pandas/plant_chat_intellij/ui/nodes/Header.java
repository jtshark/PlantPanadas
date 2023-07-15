package de.plant.pandas.plant_chat_intellij.ui.nodes;

import de.plant.pandas.chatbot.GenerationStage;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Header extends HBox {

    private final ObjectProperty<GenerationStage> stage = new SimpleObjectProperty<>(this, "stage", null);
    private final double PBSIZE = 54;

    public Header() {
        ImageView profileImageView = new ImageView();

        Image image = new Image(getClass().getResource("/PandaPB.jpeg").toExternalForm());
        profileImageView.setImage(image);

        Circle circleClip = new Circle(PBSIZE / 2, PBSIZE / 2, PBSIZE / 2);
        profileImageView.setClip(circleClip);

        profileImageView.setFitHeight(PBSIZE);
        profileImageView.setFitWidth(PBSIZE);

        VBox vBox = new VBox();
        HBox.setHgrow(vBox, Priority.ALWAYS);
        Label nameLabel = new Label("Plant Panadas");
        nameLabel.setAlignment(Pos.CENTER);
        FontHelper.bindFont(nameLabel, FontHelper.FontType.STANDARD);

        Label statusLabel = new Label();
        FontHelper.bindFont(statusLabel, FontHelper.FontType.SUBTITLE);
        statusLabel.visibleProperty().bind(stage.isNotNull());
        statusLabel.managedProperty().bind(stage.isNotNull());

        statusLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            if (stage.get() == null) {
                return null;
            }
            return switch (stage.get()) {
                case THINKING -> "is thinking...";
                case CREATE_PLAN -> "is creating a plan...";
                case GENERATE_PLANT_UML -> "is generating Plant UML...";
                case FIX_ERRORS -> "is fixing errors...";
            };
        }, stage));


        vBox.alignmentProperty().bind(Bindings.when(stage.isNotNull())
                .then(Pos.TOP_LEFT)
                .otherwise(Pos.CENTER_LEFT));

        Region placeholderRegion = new Region();
        VBox.setVgrow(placeholderRegion, Priority.ALWAYS);
        placeholderRegion.managedProperty().bind(stage.isNotNull());

        vBox.getChildren().addAll(nameLabel, placeholderRegion, statusLabel);

        setBackground(new Background(new BackgroundFill(Color.web("#202c33"), CornerRadii.EMPTY, Insets.EMPTY)));

        getChildren().addAll(profileImageView, vBox);
        setAlignment(Pos.CENTER_LEFT);
        HBox.setMargin(vBox, new Insets(10, 10, 10, 10));
        HBox.setMargin(profileImageView, new Insets(10, 10, 10, 10));


    }


    public void setStage(GenerationStage stage) {
        this.stage.set(stage);
    }
}
