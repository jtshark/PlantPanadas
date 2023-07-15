package de.plant.pandas.plant_chat_intellij.ui.nodes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Header extends HBox {
    public Header() {
        ImageView profileImageView = new ImageView();

        Image image = new Image(getClass().getResource("/PandaPB.jpeg").toExternalForm());
        profileImageView.setImage(image);

        Circle circleClip = new Circle(32, 32, 32);
        profileImageView.setClip(circleClip);

        profileImageView.setFitHeight(64);
        profileImageView.setFitWidth(64);

        Label nameLabel = new Label("Plant Panadas");
        nameLabel.setAlignment(Pos.CENTER);
        setAlignment(Pos.CENTER_LEFT);
        FontHelper.bindFont(nameLabel);


        this.setBackground(new Background(new BackgroundFill(Color.web("#202c33"), CornerRadii.EMPTY, Insets.EMPTY)));

        getChildren().addAll(profileImageView, nameLabel);
        HBox.setMargin(profileImageView, new Insets(10, 10, 10, 10));
    }
}
