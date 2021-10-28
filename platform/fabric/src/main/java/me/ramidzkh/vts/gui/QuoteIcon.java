package me.ramidzkh.vts.gui;

import io.github.astrarre.rendering.internal.Renderer2DImpl;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import io.github.astrarre.rendering.v1.edge.Stencil;

public record QuoteIcon(float width, float height, Icon icon) implements Icon {

    @Override
    public void render(Render3d render) {

        render.fill().rect(0xff8b8b8b, 0, 0, this.width, this.height);
        // top shade
        render.fill().rect(0xff373737, 0, 0, this.width - 1, 1);
        render.fill().rect(0xff373737, 0, 0, 1, this.height - 1);
        // bottom shade
        render.fill().rect(0xffffffff, this.width - 1, 1, 1, this.height - 1);
        render.fill().rect(0xffffffff, 1, this.height - 1, this.width - 1, 1);

        int i = Renderer2DImpl.STENCIL.startStencil(Stencil.Type.PASSTHROUGH);
        Stencil stencil = Renderer2DImpl.STENCIL;
        icon.render(render);
        render.flush();
        stencil.fill(i);
        render.fill().rect(0x77222222, 0, 0, 16, 16);
        render.flush();
        stencil.endStencil(i);
    }
}
