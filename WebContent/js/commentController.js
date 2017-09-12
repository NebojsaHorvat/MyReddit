function reportComment(subforum,topic,commentId,commentAuthor){
	
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	if( u != null)
    	{
     		
    		reportInfo.operation="reportComment";
    		reportInfo.subforum=subforum;
    		reportInfo.topic=topic;
    		reportInfo.commentId=commentId;
    		reportInfo.commentAuthor=commentAuthor;
    		
    		$('#report-modal').modal('toggle');
    		
    	}else{
    		alert("Need to login in order to report");
    	}
    }); 
	
	
	
}


function saveComment(subforum,topic,commentId,commentAuthor)
{
	
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	if( u != null)
    	{
    		
    		$.ajax({
				type:"post",
		        url:subforumService +"saveComment/"+subforum+"/"+topic+"/"+commentId,
		    });
    	}else{
    		alert("Need to login in order to save");
    		
    	}
    });
}


function editComment(subforum,topic,commentId,commentAuthor)
{
	
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	if( u != null)
    	{
    		
    		
    		
    		if(u.role == "ADMIN" || u.role=="MODERATOR" || u.username==commentAuthor)
			{
    			//commentInfo.operation="editComment";subforum:subforum,topic:topic, commentId: commentId, commentAuthor: commentAuthor,text: text}
    			commentInfo.operation="editComment"
    			commentInfo.subforum = subforum;
    			commentInfo.topic=topic;
    			commentInfo.commentId = commentId;
    			commentInfo.commentAuthor = commentAuthor;
    			commentInfo.text="";
    			
    			
			}
    		else 
			{
    			alert("You have no right to edit this comment");
    			$('#comment-modal').modal('toggle');
	    	}
			 
    	}else{
    		alert("Need to login in order to delete");
    		$('#comment-modal').modal('toggle');
    	}
    });
}
function deleteComment(subforum,topic,commentId,commentAuthor)
{
	
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	if( u != null)
    	{
    		if(u.role == "ADMIN" || u.role=="MODERATOR" || u.username==commentAuthor)
			{
    			
        			$.ajax({
        				type:"delete",
    			        url:subforumService +"deleteComment/"+subforum+"/"+topic+"/"+commentId,
        		    });
        			
        			topicClicked(subforum,topic)
        			

			}
    		else 
			{
    			alert("You have no right to delete this comment");
			}
			
    	}else{
    		alert("Need to login in order to delete");
    	}
    }); 
	

}
function commentOnComment(subforum,topic,commentId)
{
	
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	if( u != null)
    	{
    		commentInfo.operation="editCommentOnComment"
    		commentInfo.subforum = subforum;
			commentInfo.topic=topic;
			commentInfo.commentId = commentId;
			commentInfo.commentAuthor = "";
			commentInfo.text="";
    		 		
			
    	}else{
    		alert("Need to login in order to comment");
    		$('#comment-modal').modal('toggle');
    	}
    }); 
	

}



function commentDisliked(subforum,topic,commentId)
{
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	if( u != null)
    	{
	    	
			var disliked=false;
			for(var i=0; i <u.dislikedComments.length; i++)
			{
				if(u.dislikedComments[i] == commentId)
					disliked=true;
			}
			if(disliked)
			{
				alert("comment already disliked");
				return;
			} 
				
			var liked=false;
			for(var i=0; i <u.likedComments.length; i++)
			{
				if(u.likedComments[i] == commentId)
				{
					$.ajax({
						type:"post",
				        url:usersService+"updateUser/likedComments/delete/"+commentId,
				    }).then(function (message) { 	
				        

				    });
					
					
					$.ajax({
						type:"post",
				        url:subforumService +"updateComment/"+subforum+"/"+topic+"/"+commentId+"/likes/decl",
				    }).then(function (message) { 	
				        

				    });
				}
					
			}
			//u.likedTopics.push(topic);
			
			$.ajax({
				type:"post",
		        url:usersService+"updateUser/dislikedComments/add/"+commentId,
		    }).then(function (message) { 	
		       
		    });
			
			
			$.ajax({
				type:"post",
		        url:subforumService +"updateComment/"+subforum+"/"+topic+"/"+commentId+"/dislikes/incl",
		    }).then(function () { 	
		       
		    });
			
			topicClicked(subforum,topic)
			
			
    	}else{
    		alert("Need to login in order to like");
    	}
    }); 
	
}

function commentLiked(subforum,topic,commentId)
{
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	if( u != null)
    	{
	    	
			var liked=false;
			for(var i=0; i <u.likedComments.length; i++)
			{
				if(u.likedComments[i] == commentId)
					liked=true;
			}
			if(liked)
			{
				alert("comment already liked");
				return;
			} 
				
			var disliked=false;
			for(var i=0; i <u.dislikedComments.length; i++)
			{
				if(u.dislikedComments[i] == commentId)
				{
					$.ajax({
						type:"post",
				        url:usersService+"updateUser/dislikedComments/delete/"+commentId,
				    }).then(function (message) { 	
				        

				    });
					
					
					$.ajax({
						type:"post",
				        url:subforumService +"updateComment/"+subforum+"/"+topic+"/"+commentId+"/dislikes/decl",
				    }).then(function (message) { 	
				        

				    });
				}
					
			}
			//u.likedTopics.push(topic);
			
			$.ajax({
				type:"post",
		        url:usersService+"updateUser/likedComments/add/"+commentId,
		    }).then(function (message) { 	
		        
		    });
			
			
			$.ajax({
				type:"post",
		        url:subforumService +"updateComment/"+subforum+"/"+topic+"/"+commentId+"/likes/incl",
		    }).then(function () { 	
		        

		    });
			
			topicClicked(subforum,topic)
			
			
    	}else{
    		alert("Need to login in order to like");
    	}
    }); 
	
}