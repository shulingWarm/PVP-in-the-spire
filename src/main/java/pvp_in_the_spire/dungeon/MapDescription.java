package pvp_in_the_spire.dungeon;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;

import java.util.ArrayList;

//用于添加地图节点
public class MapDescription {

    public static final int MAX_ROW = 19;
    public static final int MAX_COL = 3;//目前只考虑三列的情况

    //每一行每一列的地图信息
    MapRoomNode[][] nodeArray = new MapRoomNode[MAX_ROW][MAX_COL];
    //目前的最大层数
    public int currMaxRow = 0;
    //是否已经连接过节点了
    public boolean linkedFlag = false;

    //无效的地图
    public static final int INVALID_NODE = 0;
    //随机房间
    public static final int RANDOM_NODE = -1;
    //商店
    public static final int SHOP_NODE = 1;
    //火堆
    public static final int REST_NODE = 2;
    //宝箱
    public static final int RELIC_NODE = 3;
    //事件
    public static final int EVENT_NODE = 4;

    //连接某两个node
    public static void connectNode(MapRoomNode src, MapRoomNode dst)
    {
        src.addEdge(new MapEdge(src.x, src.y, src.offsetX, src.offsetY, dst.x, dst.y, dst.offsetX, dst.offsetY, false));
    }

    MapDescription()
    {
        //把每个节点先初始化成空的
        for(int idRow=0;idRow<MAX_ROW;++idRow)
        {
            for(int idCol=0;idCol<MAX_COL;++idCol)
                nodeArray[idRow][idCol]=null;
        }
    }

    //生成一个随机房间类型
    public static int getRandomRoomType()
    {
        int idRandom = AbstractDungeon.cardRng.random(100);
        if(idRandom<35)
            return REST_NODE;
        if(idRandom<65)
            return EVENT_NODE;
        if(idRandom<90)
            return RELIC_NODE;
        return SHOP_NODE;
    }

    //根据房间类型获取具体的房间
    public static MapRoomNode getRoomInstance(int nodeFlag,int idRow,int idCol)
    {
        //如果是随机的，就把它改成具体的房间
        if(nodeFlag==RANDOM_NODE)
            nodeFlag = getRandomRoomType();
        //房间节点
        MapRoomNode tempNode = new MapRoomNode(idCol+2,idRow);
        switch (nodeFlag)
        {
            case SHOP_NODE:
                tempNode.room = new ShopRoom();
                break;
            case REST_NODE:
                tempNode.room = new RestRoom();
                break;
            case RELIC_NODE:
                tempNode.room = new TreasureRoom();
                break;
            default:
                tempNode.room = new EventRoom();
        }
        return tempNode;
    }

    //添加节点
    public void addNode(int nodeFlag,int idRow,int idCol)
    {
        if(idRow>=MAX_ROW || idCol>=MAX_COL)
            return;
        nodeArray[idRow][idCol]=getRoomInstance(nodeFlag,idRow,idCol);
        if(idRow>currMaxRow)
            currMaxRow = idRow;
    }

    //添加boss节点
    public void addBossNode()
    {
        //最后的boss房间
        MapRoomNode bossNode = new MapRoomNode(3,currMaxRow+1);
        bossNode.room = new MonsterRoomBoss();
        nodeArray[currMaxRow+1][1]=bossNode;
        //胜利结算的房间
        MapRoomNode victoryNode = new MapRoomNode(3,currMaxRow+2);
        victoryNode.room = new TrueVictoryRoom();
        nodeArray[currMaxRow+2][1]=victoryNode;
    }

    //连接交叉线
    public void crossLink4(MapRoomNode node11,MapRoomNode node12,
           MapRoomNode node21,MapRoomNode node22)
    {
        if(FakeEnding.randomJudge())
        {
            connectNode(node11,node22);
        }
        else {
            connectNode(node12,node21);
        }
    }

    //连接所有的节点
    public void connectNodes()
    {
        if(linkedFlag)
            return;
        for(int idRow=1;idRow<=currMaxRow;++idRow)
        {
            for(int idCol=0;idCol<MAX_COL;++idCol)
                connectNode(nodeArray[idRow-1][idCol],nodeArray[idRow][idCol]);
            //在4个节点之间连出一条交叉线
            crossLink4(nodeArray[idRow-1][0],nodeArray[idRow-1][1],
                nodeArray[idRow][0],nodeArray[idRow][1]);
            //如果是第一行，必须是右上连左下
            if(idRow==1)
            {
                connectNode(nodeArray[idRow-1][1],nodeArray[idRow][2]);
            }
            else {
                crossLink4(nodeArray[idRow-1][1],nodeArray[idRow-1][2],
                        nodeArray[idRow][1],nodeArray[idRow][2]);
            }
        }
        //最后收尾，让boss房间连接所有
        for(int idCol=0;idCol<MAX_COL;++idCol)
        {
            connectNode(nodeArray[currMaxRow][idCol],nodeArray[currMaxRow+1][1]);
        }
        linkedFlag=true;
    }

    //生成地图
    public ArrayList<ArrayList<MapRoomNode>> generateMap()
    {
        addBossNode();
        connectNodes();
        //初始化最后显示的结果
        ArrayList<ArrayList<MapRoomNode>> map = new ArrayList<>();
        //逐个添加每一层
        for(int idRow=0;idRow<=currMaxRow+2;++idRow)
        {
            //本层的节点
            ArrayList<MapRoomNode> rowNodes = new ArrayList<>();
            //先添加0和1
            rowNodes.add(new MapRoomNode(0,idRow));
            rowNodes.add(new MapRoomNode(1,idRow));
            //判断是否需要插入左边
            if(nodeArray[idRow][0]!=null)
                rowNodes.add(nodeArray[idRow][0]);
            rowNodes.add(new MapRoomNode(2,idRow));
            if(nodeArray[idRow][1]!=null)
                rowNodes.add(nodeArray[idRow][1]);
            rowNodes.add(new MapRoomNode(4,idRow));
            if(nodeArray[idRow][2]!=null)
                rowNodes.add(nodeArray[idRow][2]);
            rowNodes.add(new MapRoomNode(5,idRow));
            rowNodes.add(new MapRoomNode(6,idRow));
            //把当前行添加到地图里
            map.add(rowNodes);
        }
        return map;
    }


}
