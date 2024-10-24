package pvp_in_the_spire.ui;

import pvp_in_the_spire.events.DelayRequestEvent;
import pvp_in_the_spire.GlobalManager;
import pvp_in_the_spire.pvp_api.Communication;
import pvp_in_the_spire.helpers.FontLibrary;
import pvp_in_the_spire.network.PlayerInfo;
import pvp_in_the_spire.patches.connection.InputIpBox;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

import java.io.DataInputStream;
import java.io.IOException;

//用于显示延迟信息的框
public class DelayBox extends AbstractPage {

    //用于显示延迟数值的textlabel
    public TextLabel label;

    //上次发送延迟信息的时间
    public long lastSendTime = 0;

    //上次检查的时间
    public long lastCheckTime = 0;

    //发送的消息tag
    public int messageTag = 30000;

    //进行延迟测试的时间间隔
    public static final long TEST_GAP = 500;

    //上次发送的延迟消息是否已经收到回复
    public boolean lastMessageRepliedFlag = true;

    //超时重发的时间间隔
    public long reSendGap = TEST_GAP*4;

    //当前正在访问的player tag
    public static int targetPlayerTag;

    //上一次切换目标的时间
    public long lastChangeTargetTime = 0;

    //目标玩家的名字显示框
    public TextLabel targetName;

    public DelayBox()
    {
        //固定box的位置
        this.x = Settings.WIDTH*0.6f;
        this.y = Settings.HEIGHT*0.95f;
        this.width = Settings.WIDTH*0.1f;
        this.height = Settings.HEIGHT*0.05f;
        this.label = new TextLabel(this.x,this.y,this.width,this.height,"0",
            InputIpBox.generateFont(26));
        this.label.isLeftAlign = true;
        this.targetName = new TextLabel(this.x,this.y,this.width,this.height,"", FontLibrary.getFontWithSize(26));
        this.targetName.isLeftAlign = true;
    }

    //增加tag标号
    public void improveTag()
    {
        messageTag++;
        if(messageTag>300000)
        {
            messageTag = 30000;
        }
    }

    //之前的消息收到了回复的处理
    public void messageRepliedTrigger()
    {
        //获取当前的时间
        long currTime = System.currentTimeMillis();
        //更新时间显示
        updateDelayText(currTime - lastSendTime);
        //更新说明表示信息已经被回复了
        this.lastMessageRepliedFlag = true;
        //恢复超时重发的时间间隔
        reSendGap = TEST_GAP*5;
        improveTag();
    }

    //接收对方的响应信息
    public void receiveResponse(int senderTag,int requestTag)
    {
        //如果发送者不是自己关注的人，那就不用管
        if(targetPlayerTag != senderTag)
        {
            return;
        }
        //判断是不是自己等待的tag
        if(requestTag==this.messageTag)
        {
            messageRepliedTrigger();
        }
    }


    //对延迟信息的处理
    public void receiveDelayInfo(DataInputStream streamHandle)
    {
        try
        {
            //接收要处理的tag
            int receivedTag = streamHandle.readInt();
            //System.out.printf("receive tag %d\n",receivedTag);
            //判断是不是需要被回复的tag 数据必须有一个合理的范围
            if(receivedTag<-10000 && receivedTag>-310000)
            {
                sendNewTest(-receivedTag);
            }
            //否则判断是不是收到了自己之前的回复
            else if(receivedTag==this.messageTag)
            {
                messageRepliedTrigger();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //发送新的延迟测试消息
    public void sendNewTest(int sendingTag)
    {
        this.lastMessageRepliedFlag = false;
        Communication.advanceSendEvent(new DelayRequestEvent(targetPlayerTag,messageTag),
                targetPlayerTag);
    }

    //更新显示的延迟信息的文本
    public void updateDelayText(long newDelay)
    {
        //更新需要显示的时间
        int delayTime = newDelay<10000 ? (int)newDelay : 10000;
        //更新要显示的文本
        label.text = String.valueOf(delayTime);
        //根据时间的情况更改文本颜色
        if(delayTime<150)
            label.color = Color.GREEN;
        else if(delayTime<500)
            label.color = Color.ORANGE;
        else
            label.color = Color.RED;
    }

    //设置目标player
    public void setTargetPlayer(PlayerInfo info)
    {
        if(info == null)
        {
            System.out.println("Target is null");
            return;
        }
        targetPlayerTag = info.playerTag;
        //设置玩家的名字
        this.targetName.text = info.getName() + ": ";
        //设置文本框的位置
        this.targetName.x = this.label.x - FontLibrary.getTextWidth(
            targetName.text,targetName.font
        );
    }

    @Override
    public void render(SpriteBatch sb) {
        //获取当前的时间
        long currTime = System.currentTimeMillis();
        //判断是否需要发送测试
        if(currTime - lastCheckTime > TEST_GAP)
        {
            lastCheckTime = currTime;
            //判断上次发送的消息是否已经收到回复
            if(lastMessageRepliedFlag)
            {
                //这说明可以发送下次的消息了 发送负消息表示收到请回复
                sendNewTest(this.messageTag);
                //记录此时的时间
                lastSendTime = currTime;
            }
            else {
                //判断是不是距离上次发送已经超过了5倍的等待时间
                if(currTime - lastSendTime > reSendGap)
                {
                    //放弃对上次的消息的等待
                    improveTag();
                    sendNewTest(this.messageTag);
                    //增加重发的时间
                    reSendGap += TEST_GAP*3;
                }
                //更新显示的时间差的文本
                updateDelayText(currTime - lastSendTime);
            }
        }
        if(currTime - lastChangeTargetTime > 10000)
        {
            setTargetPlayer(GlobalManager.playerManager.getRandPlayer());
            lastChangeTargetTime = currTime;
        }
        //渲染要显示的文本
        label.render(sb);
        this.targetName.render(sb);
    }
}
