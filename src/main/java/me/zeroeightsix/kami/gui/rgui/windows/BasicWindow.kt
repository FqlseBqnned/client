package me.zeroeightsix.kami.gui.rgui.windows

import me.zeroeightsix.kami.module.modules.client.GuiColors
import me.zeroeightsix.kami.util.graphics.RenderUtils2D
import me.zeroeightsix.kami.util.graphics.VertexHelper
import me.zeroeightsix.kami.util.math.Vec2d
import me.zeroeightsix.kami.util.math.Vec2f

/**
 * Window with rectangle rendering
 */
open class BasicWindow(
        name: String,
        posX: Float,
        posY: Float,
        width: Float,
        height: Float,
        settingGroup: SettingGroup
) : CleanWindow(name, posX, posY, width, height, settingGroup) {

    override fun onRender(vertexHelper: VertexHelper, absolutePos: Vec2f) {
        super.onRender(vertexHelper, absolutePos)
        RenderUtils2D.drawRectFilled(vertexHelper, Vec2d(0.0, 0.0), Vec2d(renderWidth, renderHeight), GuiColors.backGround)
        RenderUtils2D.drawRectOutline(vertexHelper, Vec2d(0.0, 0.0), Vec2d(renderWidth, renderHeight), 2.5f, GuiColors.primary)
    }
}