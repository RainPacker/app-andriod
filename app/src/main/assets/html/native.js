var Native = function(){

    var navite_command=new Array();
    
    
    //只生成，不执行
    
    this.generateCommand=function(sid,act,param,begin,inprocess,successCalling,failedCalling,cancelCalling){
        
        var random_value = this.generateUnique(sid,param);
        
        var command = new Command();//命令对象
        
        command.cid = sid;//服务编号
        
        command.act_type = act; //动作种类
        
        command.param = param; //参数
        
        command.random_md5 = random_value; //随机编号
        
        command.begin = begin; //开始的回调
        
        command.inprocess = inprocess;  //执行过程中的回调
        
        command.successCalling = successCalling; //成功的回调
        
        command.failedCalling = failedCalling;  //失败的回调
        
        command.cancelCalling = cancelCalling; //取消的回调
        
        navite_command[random_value] = command; //将回调写入字典
        
        return command;

    }
    
    this.calling=function(sid,act,param,begin,inprocess,successCalling,failedCalling,cancelCalling){
        
        var random_value = this.generateUnique(sid,param);
        var command = this.generateCommand(sid,act,param,begin,inprocess,successCalling,failedCalling,cancelCalling);
       
        navite_command[random_value] = command; //将回调写入字典
        
        command.doExecute(); //执行调用
        
    };
    
    this.ExecuteWithCommand=function(command){
        
           command.doExecute(); //执行调用
        
    }
    
    //生成命令的guid
    this.generateUnique=function(sid,param){
        var paramstr = JSON.stringify(param);
        var componend_str = sid+paramstr;
        return MD5(componend_str);
    }
   
    //开始调用
    Begin = function(callbackid,result){
        var command = navite_command[callbackid];
        if(command!==null){
            command.begin(result);
        }
    }
    
    //进行中
    Inprocess=function(callbackid,result){
        var command = navite_command[callbackid];
        if(command!==null){
            command.inprocess(result);
        }
    }
    
    //执行完毕
    Success=function(callbackid,result){
        var command = navite_command[callbackid];
        if(command!==null){
            command.successCalling(result);
            delete navite_command[callbackid];
        }
    }
    
    //失败
    Failed=function(callbackid,result,error){
        var command = navite_command[callbackid];
        if(command!==null){
            command.failedCalling(result,error);
            delete navite_command[callbackid];
        }
    }
    
    //取消
    Cancel=function(callbackid,result){
        
        var command = navite_command[callbackid];
        if(command!==null){
            command.cancelCalling(result);
             delete navite_command[callbackid];
        }
    }
    
    //当页面收到通知时使用
    Notify=function(content){
        
        //这个方法所有的页面，凡是事件注册的地方，都调用该方法。
        
        SystemNotify(content);
        
    }
    
}


