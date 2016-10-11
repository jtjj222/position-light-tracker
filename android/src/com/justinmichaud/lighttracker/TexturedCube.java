package com.justinmichaud.lighttracker;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import javax.microedition.khronos.opengles.GL10;

public class TexturedCube {

    public final String name;
    public final Vector3 position;
    public final Quaternion rotation;

    private final Model model;
    public final ModelInstance instance;
    public final Texture texture;
    public final Material material;

    public TexturedCube(String name, float width, float height, float depth,
                        float repeatU, float repeatV, String tex) {
        this.name = name;

        position = new Vector3();
        rotation = new Quaternion();
        texture = new Texture(tex);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        material = new Material(TextureAttribute.createDiffuse(texture));

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder mpb = modelBuilder.part("box", GL10.GL_TRIANGLES,
                VertexAttributes.Usage.Position
                | VertexAttributes.Usage.Normal
                | VertexAttributes.Usage.TextureCoordinates, material);
        mpb.setUVRange(0, 0, repeatU, repeatV);
        BoxShapeBuilder.build(mpb, width, height, depth);
        model  = modelBuilder.end();

        instance = new ModelInstance(model);
    }

    public void update() {
        instance.transform.idt();
        instance.transform.rotate(rotation);
        instance.transform.translate(position);
    }

    public void draw(ModelBatch batch) {
        batch.render(instance);
    }

    public void dispose() {
        model.dispose();
        texture.dispose();
    }

}
