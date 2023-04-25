var Command = function(){
    
    var cid; //命令id
    
    var act_type; // 1 push 2 present 3 back 4 calling
    
    var random_md5; //随机id
    
    var param; //命令参数
    
    var begin; //开始调用
    
    var inprocess; //执行过程调用
    
    var successCalling; //成功调用
    
    var failedCalling;  //失败调用
    
    var cancelCalling; //取消调用
    

    this.doExecute=function(){
      
        var param_str = this.GenerateComponents(this.param);
    
        var str= "calling://"+this.act_type+"/"+this.cid;
        
        if(param_str.length>0){
            
            str += "?" +param_str;
        }
     
        location.href =str;
        
    };
    
    this.GenerateComponents=function(xparam){
        
       
        
        var str = "";
        for (var i in xparam) {
            
            var strx =JSON.stringify(xparam[i])
            
            str += i + "=" + Base64.encode(strx) +"&";
        }
        str+="callback="+this.random_md5;
        return str;
        
    }
    
};
