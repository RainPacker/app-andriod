var EventListener=function(){
    
    
    var event_map = new Array();
    var i=0;
    var execute_count=0;
    var native_obj = new Native();
    
    this.addEventListener=function(key){
        
        var command = this.generalFunction(key,"s27")
        
        event_map[i] = command;
        
        this.doNativeCall(execute_count);
        
        i++;
    }
    
    this.doNativeCall=function(ind){
        
        if(ind==0){
         
            var command = event_map[i];
            
            native_obj.ExecuteWithCommand(command);
            
            execute_count++;
        }
    }

    
    
    this.removeEventListener=function(key){
        
        var command = this.generalFunction(key," sds")
        
        event_map[i] = command;
      
        this.doNativeCall(execute_count);
        
        i++;
        
    }
    
    this.generalFunction=function(key,cid){
        
        var param= {eventkey:key};
        var begin=function(){
            
        }
        var runIn=function(obj){
            
        }
        var success= function(obj){
            
            var command_next = event_map[execute_count];
           
            if(execute_count<event_map.length){
             if(command_next){
                 
                native_obj.ExecuteWithCommand(command_next);
                
                 execute_count++;
             }
            }else{
                
                for(var i=0;i<event_map.length;i++){
                    
                    event_map.splice(i,1);
                    
                }
                execute_count=0;
                
            }
           
            
        }
        var failed = function(error,obj){
            
        }
        
        var command = native_obj.generateCommand(cid,"5",param,begin,runIn,success,failed);
        
        return command;

    }
    
    
    
   
}
