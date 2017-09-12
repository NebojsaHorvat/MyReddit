

function loadTopics(name)
{
	$('#subforumName').text(name);
	 $.ajax({
     	method : "get",
     	url: subforumService + 'getSubforum/' + name
     }).then(function (subforum) {
         
     	var topics = subforum.topics;
     	var table  =$("#topicsTable");
     	table.empty();
     	topics.forEach(function (topic){
				var tableRow ='<tr>'+
         	'<td id="'+topic.topicId+'"><a href="" class="topicClicked" ><h1>'+topic.name+'</h1></a></td>'+
             '</tr><tr>'+
             	'<td><button id="like2" class="glyphicon glyphicon-thumbs-up like"></button>&nbsp<p style="display:inline-block">'+topic.likes+
             	'</p>&nbsp&nbsp&nbsp<button id="dislike2" class="glyphicon glyphicon-thumbs-down dislike"></button>'+
             	 '&nbsp <p style="display:inline-block">'+topic.dislikes+'</p><p style="display:inline-block"> &nbsp&nbsp&nbspComments:&nbsp</p><p style="display:inline-block">'+
             	 topic.comments.length+'</p></td>'+
             '</tr>';
				table.append(tableRow);
				
				
     	});
     	$('.topicClicked').click(function (event) {
            event.preventDefault();
     		var topic = $(this).closest('tr').find('td:first-child').text();
            var subforum = $('#subforumName').text();
            var topicId=$(this).closest('tr').find('td:first-child').attr('id')
            
            topicClicked(subforum,topic);
            
            showWS('dispalyTopicWS')
            
            
        });
     	$('.like').click(function () {
             var topic = $(this).closest('tr').prev().find('td:first-child').find('a:first-child').text();
             var subforum = $('#subforumName').text();
             var topicId=$(this).closest('tr').find('td:first-child').attr('id')
             
             topicLiked(subforum,topic,topicId);
             
         });
     	
     	$('.dislike').click(function () {
            var topic = $(this).closest('tr').prev().find('td:first-child').find('a:first-child').text();
            var subforum = $('#subforumName').text();
            var topicId=$(this).closest('tr').find('td:first-child').attr('id')
            
            topicDisliked(subforum,topic,topicId);
            
        });
     	
     });
	}

function topicClicked(subforum,topic)
{
	$.ajax({
		type:"get",
        url:subforumService+"getTopic/"+subforum+"/"+topic,
		//url:subforumService+"getTopic/"+topicId,
    }).then(function (t){
    	if( t == undefined )
		{
    		alert("Topic Deleted");
    		return;
		}
    	$('#TopicName').text(t.name + "-" + t.parentSubforumName)
		if(t.type == "Text"){
			$('#textTypeP').text(t.content);
			
			$('#textTypeMain').show();
			$('#linkTypeMain').hide();
			$('#imageTypeMain').hide();
			
		}else if(t.type == "Link"){
			$('#linkTypeA').attr('href',t.content)
			$('#linkTypeA').text(t.content);
			
			$('#textTypeMain').hide();
			$('#linkTypeMain').show();
			$('#imageTypeMain').hide();
			
		}
		else if(t.type == "Image"){
			
			$('#imageTypeI').attr("src",t.image);
			$('#textTypeMain').hide();
			$('#linkTypeMain').hide()
			$('#imageTypeMain').show();
			
		}
    	

    	var deep=0;
    	$('#topicComments').empty();
    	dispalyComments(t.comments,deep)
    	
    	$('.likeComment').click(function (event) {
            event.preventDefault();
     		var commentId = $(this).closest('tr').attr("id");
            
     		var arr = $('#TopicName').text().split('-');
     		var topic = arr[0];
     		var subforum= arr[1];
     		
            commentLiked(subforum,topic,commentId);
            
        });
    	
    	$('.dislikeComment').click(function (event) {
            event.preventDefault();
     		var commentId = $(this).closest('tr').attr("id");
            
     		var arr = $('#TopicName').text().split('-');
     		var topic = arr[0];
     		var subforum= arr[1];
     		
            commentDisliked(subforum,topic,commentId);
            
        });
    	
    	$('.comentOnComment').click(function (event) {
            event.preventDefault();
     		var commentId = $(this).closest('tr').attr("id");
            
     		var arr = $('#TopicName').text().split('-');
     		var topic = arr[0];
     		var subforum= arr[1];
     		
            commentOnComment(subforum,topic,commentId);
            
        });
    	$('.deleteComment').click(function (event) {
            event.preventDefault();
     		var commentId = $(this).closest('tr').attr("id");
            var commentAuthor = $(this).closest('tr').attr("name");
     		var arr = $('#TopicName').text().split('-');
     		var topic = arr[0];
     		var subforum= arr[1];
     		
            deleteComment(subforum,topic,commentId,commentAuthor);
            
        });
    	
    	$('.edittComment').click(function (event) {
            event.preventDefault();
     		var commentId = $(this).closest('tr').attr("id");
     		var text = $(this).closest('h4').text();
     		$('#commentTextInput').val(text);
            var commentAuthor = $(this).closest('tr').attr("name");
     		var arr = $('#TopicName').text().split('-');
     		var topic = arr[0];
     		var subforum= arr[1];
     		
            editComment(subforum,topic,commentId,commentAuthor);
            
        });
    	
    	$('.saveComment').click(function (event) {
            event.preventDefault();
     		var commentId = $(this).closest('tr').attr("id");
            var commentAuthor = $(this).closest('tr').attr("name");
     		var arr = $('#TopicName').text().split('-');
     		var topic = arr[0];
     		var subforum= arr[1];
     		
            saveComment(subforum,topic,commentId,commentAuthor);
            checkUser();
        });
    	
    	$('.reportComment').click(function (event) {
            event.preventDefault();
     		var commentId = $(this).closest('tr').attr("id");
            var commentAuthor = $(this).closest('tr').attr("name");
     		var arr = $('#TopicName').text().split('-');
     		var topic = arr[0];
     		var subforum= arr[1];
     		
            reportComment(subforum,topic,commentId,commentAuthor);
            
        });
	});
}


function dispalyComments(comments,deep)
{
	var table = $('#topicComments');
		
	var tab="";
	for(var i=0;i<deep;i++)  
		tab+='<td style="width:1%">--------</td>';
	$.each( comments,function (index, comment){
		
		
		var tableRow = '<tr id='+comment.id+' name='+comment.author.username+'>'+tab+
				'<pre>'+'<td class="grow">'+
	        	'<h4>'+ comment.text + '<p style="color: blue">autor:'+comment.author.username+'</p></h4><br><pre style="display:inline-block">'+
	        	'<button id="like2" class="glyphicon glyphicon-thumbs-up likeComment"></button>&nbsp<p style="display:inline-block">'+comment.likes+'</p>&nbsp<button  class="glyphicon glyphicon-thumbs-down dislikeComment"></button>'+
	        	'&nbsp <p style="display:inline-block">'+comment.dislikes+'</p><p style="display:inline-block"> &nbsp<a  href="" data-toggle="modal" data-target="#comment-modal" class="comentOnComment" >Comment</a></p>'+
	        	'<p style="display:inline-block"> &nbsp<a  href="" class="reportComment" >Report</a></p>'+ '<p style="display:inline-block" >&nbsp<a  href="" class="edittComment" data-toggle="modal" data-target="#comment-modal" >Edit</a></p>'+
	        	'&nbsp<a  href="" class="deleteComment" >Delete</a>' +
	        	'&nbsp<a  href="" class="saveComment" >Save</a></p>' +'</p>'+
	        	'</td> </tr>'
	    
	    if(!comment.deleted) 
	    {
		    table.append(tableRow);
		    if(comment.childComments.length !=0)
		    {
		    	dispalyComments(comment.childComments,++deep);
		    }	
	    }
	});
	
	 
	
}



function topicDisliked(subforum,topic,wellcome)
{
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	if( u != null)
    	{
	    	
			var disliked=false;
			for(var i=0; i <u.dislikedTopics.length; i++)
			{
				if(u.dislikedTopics[i] == topic)
					disliked=true;
			}
			if(disliked)
			{
				alert("topic already disliked");
				return;
			}
				
			var liked=false;
			for(var i=0; i <u.likedTopics.length; i++)
			{
				if(u.likedTopics[i] == topic)
				{
					$.ajax({
						type:"post",
				        url:usersService+"updateUser/likedTopics/delete/"+topic,
				    }).then(function (message) { 	
				       

				    });
					
					
					$.ajax({
						type:"post",
				        url:subforumService +"updateTopic/"+subforum+"/"+topic+"/likes/decl",
				    }).then(function (message) { 	
				        

				    });
				}
					
			}
			//u.likedTopics.push(topic);
			
			$.ajax({
				type:"post",
		        url:usersService+"updateUser/dislikedTopics/add/"+topic,
		    }).then(function (message) { 	
		        

		    });
			
			
			$.ajax({
				type:"post",
		        url:subforumService +"updateTopic/"+subforum+"/"+topic+"/dislikes/incl",
		    }).then(function (message) { 	
		        

		    });
			
			if(!wellcome)
				loadTopics($("#subforumName").text());
			
			
    	}else{
    		alert("Need to login in order to like");
    	}
    }); 

}

function topicLiked(subforum,topic,wellcome)
{
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	if( u != null)
    	{
	    	
			var liked=false;
			for(var i=0; i <u.likedTopics.length; i++)
			{
				if(u.likedTopics[i] == topic)
					liked=true;
			}
			if(liked)
			{
				alert("topic already liked");
				return;
			}
				
			var disliked=false;
			for(var i=0; i <u.dislikedTopics.length; i++)
			{
				if(u.dislikedTopics[i] == topic)
				{
					$.ajax({
						type:"post",
				        url:usersService+"updateUser/dislikedTopics/delete/"+topic,
				    }).then(function (message) { 	
				        

				    });
					
					
					$.ajax({
						type:"post",
				        url:subforumService +"updateTopic/"+subforum+"/"+topic+"/dislikes/decl",
				    }).then(function (message) { 	
				        

				    });
				}
					
			}
			//u.likedTopics.push(topic);
			
			$.ajax({ 
				type:"post",
		        url:usersService+"updateUser/likedTopics/add/"+topic,
		    }).then(function (message) { 	
		       

		    });
			
			
			$.ajax({
				type:"post",
		        url:subforumService +"updateTopic/"+subforum+"/"+topic+"/likes/incl",
		    }).then(function (message) { 	
		        

		    });
			
			if(!wellcome)
				loadTopics($("#subforumName").text());
			
			
    	}else{
    		alert("Need to login in order to like");
    	}
    }); 

}