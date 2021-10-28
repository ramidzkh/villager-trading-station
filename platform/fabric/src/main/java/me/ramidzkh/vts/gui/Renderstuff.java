package me.ramidzkh.vts.gui;

import io.github.astrarre.rendering.internal.Renderer2DImpl;
import io.github.astrarre.rendering.v1.api.plane.Transform2d;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.edge.Stencil;

public record Renderstuff(float width, float height, Icon icon) implements Icon {

    @Override
    public void render(Render3d render) {
        int i = Renderer2DImpl.STENCIL.startStencil(Stencil.Type.PASSTHROUGH);
        Stencil stencil = Renderer2DImpl.STENCIL;
        icon.render(render);
        icon.transform(Transform2d.translate(1, 1));
        render.flush();
        stencil.fill(i);
        render.fill().rect(0x77222222, 0, 0, 18, 18);
        render.flush();
        stencil.endStencil(i);
    }
}
