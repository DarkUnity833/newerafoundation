package net.darkunity.newerafoundation.client.gui;
import net.darkunity.newerafoundation.inventory.TieredShulkerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
public class TieredShulkerScreen extends AbstractContainerScreen<TieredShulkerMenu>{
 private static final ResourceLocation SLOTS=ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
 public TieredShulkerScreen(TieredShulkerMenu m,Inventory i,Component t){super(m,i,t);int rows=m.getTier().rows,cols=m.getTier().cols;imageWidth=Math.max(176,cols*18+16);imageHeight=18+rows*18+14+94;titleLabelX=8;titleLabelY=6;inventoryLabelX=8;inventoryLabelY=imageHeight-94+2;}
 protected void renderBg(GuiGraphics g,float pt,int mx,int my){int x=leftPos,y=topPos,w=imageWidth,h=imageHeight;g.fill(x,y,x+w,y+h,0xffc6c6c6);g.fill(x,y,x+w,y+1,0xffffffff);g.fill(x,y,x+1,y+h,0xffffffff);g.fill(x+w-1,y,x+w,y+h,0xff555555);g.fill(x,y+h-1,x+w,y+h,0xff555555);for(Slot s:menu.slots)g.blit(SLOTS,x+s.x-1,y+s.y-1,7,17,18,18);}
 public void render(GuiGraphics g,int mx,int my,float pt){super.render(g,mx,my,pt);renderTooltip(g,mx,my);}
}
