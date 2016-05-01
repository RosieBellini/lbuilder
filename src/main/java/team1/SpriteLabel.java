package team1;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

public class SpriteLabel extends JLabel{
    private static final long serialVersionUID = 1L;
    private MapPanel mapPanel;
    private Coordinate position;
    private static SokobanObject paletteState = SokobanObject.WALL;
    private static SokobanMap.Mover mover;

    public SpriteLabel(Coordinate position, MapPanel mapPanel) {
        super();
        this.position = position;
        this.mapPanel = mapPanel;

        addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent me)
            {
                if (!mapPanel.getPlayable()) {
                    modifyCell(me);
                } else {
                    SokobanMap map = mapPanel.getSokobanMap();
                    if (map.neighbours(position).contains(map.getState().getPlayerPos())){
                        map.move(position.add(map.getState().getPlayerPos().reverse()));
                        GamePanel.redraw();
                    } else {
                        if (!(map.getIsCurrentlyMoving() || map.getIsDoingSolution())) {
                            mover = map.new Mover(position, 75);
                            mover.start();
                        } else if (map.getIsCurrentlyMoving() && !map.getIsDoingSolution()) {
                            mover.interrupt();

                            while (!mover.isInterrupted()) {
                                mover.interrupt();
                            }

                            mover = map.new Mover(position, 75);
                            mover.start();
                        }
                    }
                }
            }

            public void mouseEntered(MouseEvent me) {
                if (!mapPanel.getPlayable()) {
                    if (me.getButton() == MouseEvent.NOBUTTON) {
                        if (mapPanel.getSokobanMap().get(position) == SokobanObject.SPACE) {
                            setIcon(mapPanel.getIconMap().get("DEFAULT_HOVER"));
                        }
                    } else {
                        modifyCell(me);
                    }
                }
            }

            public void mouseExited(MouseEvent me) {
                if (!mapPanel.getPlayable()) {
                    if (mapPanel.getSokobanMap().get(position) == SokobanObject.SPACE) {
                        setIcon(mapPanel.getIconMap().get("DEFAULT"));
                    } else {
                        setIcon(mapPanel.getIconMap().get(mapPanel.getSokobanMap().get(position).name()));
                    }
                }
            }
        });
    }

    private void modifyCell(MouseEvent me) {
        mapPanel.getSokobanMap().storeState();
        if (me.getButton() == MouseEvent.BUTTON1) {
            mapPanel.getSokobanMap().put(paletteState, position);
        } else if (me.getButton() == MouseEvent.BUTTON3) {
            if (mapPanel.getSokobanMap().get(position) != SokobanObject.PLAYER && mapPanel.getSokobanMap().get(position) != SokobanObject.PLAYER_ON_GOAL) {
                mapPanel.getSokobanMap().removeLayer(position);
            }
        }

        GamePanel.redraw();
        mapPanel.getSokobanMap().clearRedoStack();
    }

    public static void setPaletteState(SokobanObject paletteState) {
        SpriteLabel.paletteState = paletteState;
    }

    public static SokobanMap.Mover getMover() {
        return mover;
    }
}
