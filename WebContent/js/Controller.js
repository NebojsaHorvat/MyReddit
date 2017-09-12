var loginService = "/MyReddit/rest/loginService/";
var subforumService = "/MyReddit/rest/subforumService/"; 
var usersService = "/MyReddit/rest/usersService/";  
var messagesService = "/MyReddit/rest/messagesService/"; 
$(document).ready(function () {

checkUser(true);
$('#imageContentDiv').hide();

commentInfo={operation:"editComment",subforum:"woot",topic:"woot", commentId: "woot", commentAuthor: "woot",text: "woot"}

reportInfo={operation:"woot",subforum:"woot",topic:"woot", commentId: "woot", commentAuthor: "woot",text: "woot"}



$('#editTopicButton').click(function(event){
	event.preventDefault();

	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	
   if(u != null){
	   
	   var name= $('#TopicName').text();
	   var arr =name.split('-');
	   var topic = arr[0];
	   var subforum= arr[1];
	   
	   $.ajax({
			type:"get",
	        url:subforumService+"getTopic/"+subforum+"/"+topic,
	    }).then(function (t){
		
	    	if(t != undefined){
		    	if(u.role == "ADMIN" || u.role=="MODERATOR" || u.username == t.author.username)
				{
		    		$("#topicNameInput").val(t.name);
		    		$('#topicInputType option:selected').val(t.type);
		    		$('#textContent').val(t.content);
		    		$('#linkContent').val(t.content);
		    		$('#imageContent').val(t.image);
		    		
		    		$.ajax({
						type:"delete",
				        url:subforumService +"deleteTopic/"+subforum+"/"+topic,
				    })
	    			
	    			$('#subforumName').text(t.parentSubforumName)
				   	$('#topic-modal').modal('toggle');
				   	
				}else
				{
					alert("Have no right to edit this topic");
				}
		    	
	    	}else
	    		alert("Topic not found")
	    });
   }else {
   	alert("Need to login in order to editTopic")
   }

	})

})





$('#addTopicButton').click(function(event){
	event.preventDefault();

	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	
   if(u != null){

   	$('#topic-modal').modal('toggle');
   	$("#topicNameInput").val("");
   	
   }else {
   	alert("Need to login in order to addTopic")
   }

	})

})

$('#addTopicSubmitButton').click(function (event){
	
	event.preventDefault();
	
	
   
   		$('#topic-modal').modal('toggle');
		var name = $("#topicNameInput").val();
		var type = $('#topicInputType option:selected').text();
		var textContent= $('#textContent').val();
		var linkContent= $('#linkContent').val();
		var imageContent= $('#imageContent').val();
		
		var objFile = $('#ikonica');
	    var file = objFile[0].files[0];
		
		var subforumName= $('#subforumName').text();
		
		
		var fileName="";
		if(file != undefined)
			fileName=file.name;
		
		if(name == ""){
			alert("Name of Topic must be entered");
			return ;
		}else if(type == "Text" && textContent == "")
		{
			alert("Text content must be entered");
			return ;
		}else if(type == "Link" && linkContent == "")
		{
			alert("Link content must be eneterd");
			return ;
		}else if(type == "Image" && fileName == "")
		{
			alert("Image must be chosen");
			return ;
		}
		
		
		//SEND
		
		
		
	    $.ajax({
			async : false,
			type : "POST",
			url : subforumService +"addTopic",
			contentType : "application/json",
			dataType : "text",
			data : JSON.stringify({
				"name" : name, 
				"subforumName" : subforumName,
				"typeOfTopic" : type,
				"content" : textContent,
				"link" : linkContent,
				"imageName" : fileName,
			}),
			success : function(data) {
					
					if(file != undefined){
						$.ajax({
							async : false,
							type : "POST",
							url : subforumService +"addImage/" + data,
					        contentType : "multipart/form-data",
					        data: file,
					        processData: false,
						});
						
					}
				}
			
		});
		
	    displaySubforum(subforumName);
	
});




$("#displatReportsButton").click(function(event){
	event.preventDefault();
	
	var container = $('#reportsContainer');
	container.empty();
	
	
	$.ajax({
		type:"get",
        url:subforumService+"getReports"
    }).then(function (reports) {
    
    	$.each(reports,function(index,report)
    	{
    		var entity = report.typeOfRepoart;
    		if(entity == "comment")
			{
    			entity = ': ('+entity +' '+report.topic+"-"+report.subforum+  ' ) ' +report.data;
			}
    		else if(entity == "subforum")
			{
    			entity = ': ('+entity +' ) ' +report.subforum;
			}
    		else if(entity == "topic")
			{
    			entity = ': ('+entity +' ) '+  report.topic+"-"+report.subforum;
			}
    		
    		var row='<div class="row well">'+
		    '<div class="col" style="widtd:200px">'+
		    report.text+
		    '</div>'+
		    '<div class="col">'+
		    	'<div style="color:blue">Sender: '+report.user.username+'</div>'+
		    '</div >'+
		    '<div class="col">'+
	    	'<div style="color:blue">Entity : '+entity+'</div>'+
	    	'</div >'+
		    '<div class="col" style="display:inline-block" >'+
		    '<a href="" class="deleteEntityClass" name="'+report.reportId+'">Delete Entity</a>&nbsp&nbsp&nbsp'+
		    '<a href="" class="warnAuthorClass" name="'+report.reportId+'">Warn author</a>&nbsp&nbsp&nbsp'+
		    '<a href="" class="rejectClass" name="'+report.user.username+'">Reject</a>&nbsp&nbsp&nbsp'+
		    '<a href="" class="deleteReportClass" name="'+report.reportId+'">Delete report</a>&nbsp&nbsp&nbsp'+
		    '</div>'+
		    '</div>';
    		
    		container.append(row);
    	});
    	
    	
    	$('.warnAuthorClass').click(function (event){
    		
    		event.preventDefault();
    		
    		var reportId = event.currentTarget.name;
    		
     		$.ajax({
				type:"post",
		        url:messagesService +"warnAuthor/"+reportId,
		        success: function () {
		            alert("Author worned")
		        },
		        error: function (data) {
		            alert('An error occurred.');
		        },
		    });
     		
     		$("#displatReportsButton").trigger('click');
     		checkUser();
			
    	});
    	
    	$('.rejectClass').click(function(event){
			event.preventDefault();
			var username = event.currentTarget.name;
			$('#messageText').text("");
			$('#usernameSend').val(username);
			$('#sendMessage-modal').modal('toggle');
		})
		
		
    	$('.deleteReportClass').click(function (event){
    		
    		event.preventDefault();
    		
    		var reportId = event.currentTarget.name;
    		
     		$.ajax({
				type:"delete",
		        url:subforumService +"deleteReport/"+reportId,
		    });
     		
     		$("#displatReportsButton").trigger('click');
			
    	});
    	
    	$('.deleteEntityClass').click(function (event){
    		
    		event.preventDefault();
    			
    		var reportId = event.currentTarget.name;
    		
     		$.ajax({
				type:"delete",
		        url:subforumService +"deleteEntity/"+reportId,
		        success: function () {
		            alert("Entity deleted")
		        },
		        error: function (data) {
		            alert('An error occurred.');
		        },
		    });
     		
    	});
    	
    		
    	
		
    }); 
	
	
	showWS("reportsWS");
})

$('#reportForm').submit(function (evenet){

	event.preventDefault();
	
	if( reportInfo.operation == "reportSubforum"){
		
		var text = $('#reportTextInput').val();
		$.ajax({
			type:"post",
	        url:subforumService +"reportSubforum/"+reportInfo.subforum+"/"+text+".",
	        success: function (data) {
	            alert(data)
	        }
	    });
		
	}
	else if( reportInfo.operation == "reportTopic" )
	{
		var text = $('#reportTextInput').val();
		$.ajax({
			type:"post",
	        url:subforumService +"reportTopic/"+reportInfo.subforum+"/"+reportInfo.topic+"/"+text+".",
	        success: function (data) {
	            alert(data)
	        }
	    });			
		
	}
	else if( reportInfo.operation == "reportComment" )
	{
		var text = $('#reportTextInput').val();
		$.ajax({
			type:"post",
	        url:subforumService +"reportComment/"+reportInfo.subforum+"/"+reportInfo.topic+"/"+reportInfo.commentId+"/"+text+".",
	        success: function (data) {
	            alert(data)
	        }
	    });
	}
	
})




$('#reportSubforum').click(function(event){
	
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	if( u != null)
    	{
    		var name = $('#subforumName').text();
    		reportInfo.operation="reportSubforum";
    		reportInfo.subforum=name;
    		$('#report-modal').modal('toggle');
    		
    	}else{
    		alert("Need to login in order to report");
    	}
    }); 
	
})

$('#reportTopicButton').click(function(event){
	
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	if( u != null)
    	{
    		var name = $('#TopicName').text();
    		var arr =name.split('-');
     		var topic = arr[0];
     		var subforum= arr[1];
     		
    		reportInfo.operation="reportTopic";
    		reportInfo.subforum=subforum;
    		reportInfo.topic=topic;
    		$('#report-modal').modal('toggle');
    		
    	}else{
    		alert("Need to login in order to report");
    	}
    }); 
	
})


$('#followSubforum').click(function (evenet){
	event.preventDefault();

	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	
	   if(u != null){
		   
	   
			name=$('#subforumName').text();
			 $.ajax({
			        type: "post",
			        url: subforumService+"followSubforum/"+name ,
			        success: function (data) {
			            alert(data)
			            checkUser();
			        },
			        error: function (data) {
			            alert('An error occurred.');
			        },
			    });
	   }
	   else 
		   alert("Need to login in order to follow")
   });
})



$('#displayMessages').click(function(event){
	
	event.preventDefault();
	$.ajax({
		type:"post",
        url:messagesService+"allRead",
    })
    $('#unreadMessages').text(0);
    showWS("inboxWS");
	
	
});


$('#sendMessageForm').submit(function(event){
	
	event.preventDefault();
	var form = $('#sendMessageForm');
	$('#sendMessage-modal').modal('toggle');
	$.ajax({
		type: form.attr('method'),
		url: form.attr('action'),
		data: form.serialize(),
		success: function(data) {
			alert(data);
			checkUser(false);
		},
		error: function(data) {
			alert('An error occured');
		},
	});
	
	
	
});


$('#searchForm').submit(function (event){

	event.preventDefault();
	
	var form = $("form#searchForm");
	var typeOfSearch = $('#typeOfSearch').val();
	 
	 if(typeOfSearch == "Subforum" )
     {
		 $.ajax({
		        type: "post",
		        url:subforumService +"searchSubforum",
		        data: form.serialize(),
		        success: function (data) {
		        	if(data.length != 0)
	        		{		  
		        		$('#subforumSearchTable').empty();
			            $.each(data, function (indec, subforum){
			            	
			            	var tableRow='<tr><td >'+
			            	'<div class="well"><a href="" class="subforumSearchClass"><h2>'+subforum.name+'</h2></a><br>'+
			            	'<p>'+subforum.description+'</p></div>'+
			            	'</td></tr>';
			            	
			            	$('#subforumSearchTable').append(tableRow);
			            });	
			            
			            showWS("subforumSearchWS");
			           
			            
			            $('.subforumSearchClass').click(function (event) {
			            	event.preventDefault();
			            	
			                var subforum = $(this).closest('tr').find('td:first-child').find('a:first-child').text();
			                
			                
			                $('#subforumSearchWS').hide();
			                
			                displaySubforum(subforum);
			                
			                
			            });
	        		}
		        	else{
		        		alert("No items mach search input");
		        	}
		        },
		        error: function (data) {
		            alert('An error occurred.');
		        },
		    });	 
		 
		 
     }
	 if(typeOfSearch == "Topic" )
     {
			 $.ajax({
		        type: "post",
		        url:subforumService +"searchTopic",
		        data: form.serialize(),
		        success: function (data) {
		        	if(data.length != 0)
	        		{		  
		        		$('#subforumSearchTable').empty();
			            $.each(data, function (indec, topic){
			            	
			            	var tableRow='<tr><td >'+
			            	'<div class="well"><a href="" class="topicSearchClass"><h2>'+topic.name+'-'+topic.parentSubforumName +'</h2></a><br>'+
			            	'<p>'+topic.author.username+'</p></div>'+
			            	'</td></tr>';
			            	
			            	$('#subforumSearchTable').append(tableRow);
			            });	
			            
			            showWS("subforumSearchWS");
			            
			            
			            $('.topicSearchClass').click(function (event) {
			            	event.preventDefault();
			            	
			                var name = $(this).closest('tr').find('td:first-child').find('a:first-child').text();
			                var arr =name.split('-');
			         		var topic = arr[0];
			         		var subforum= arr[1];
			                
			                
			                
			                topicClicked(subforum,topic);
			                showWS("dispalyTopicWS");
			                
			                
			                
			            });
	        		}
		        	else{
		        		alert("No items mach search input");
		        	}
		        },
		        error: function (data) {
		            alert('An error occurred.');
		        },
		    });	 
     }
	 
	 if(typeOfSearch == "Username" )
     {
			 $.ajax({
		        type: "post",
		        url:usersService +"searchUsers",
		        data: form.serialize(),
		        success: function (data) {
		        	if(data.length != 0)
	        		{		  
		        		$('#subforumSearchTable').empty();
			            $.each(data, function (indec, user){
			            	
			            	var tableRow='<tr><td >'+
			            	'<div class="well"><h4>'+user.username +'</h4><br>'+
			            	'</div>'+
			            	'</td></tr>';
			            	
			            	$('#subforumSearchTable').append(tableRow);
			            });	
			            
			            showWS("subforumSearchWS");
			            
			        
	        		}
		        	else{
		        		alert("No items mach search input");
		        	}
		        },
		        error: function (data) {
		            alert('An error occurred.');
		        },
		    });	 
     }
	
});

$('#CommentForm').submit(function (event){
	
	event.preventDefault();
	
	if( commentInfo.operation == "addCommentToTopic"){
		event.preventDefault();
		var text=$("#commentTextInput").val();
		var arr = $('#TopicName').text().split('-');
			var topic = arr[0];
			var subforum= arr[1];
		$.ajax({
			type:"post",
	        url:subforumService +"addComment/"+subforum+"/"+topic+"/"+text,
	    });
		
		topicClicked(subforum,topic)
	}
	else if( commentInfo.operation == "editComment" )
	{
		
		
		var text=$("#commentTextInput").val();
			$.ajax({
				type:"post",
		        url:subforumService +"editComment/"+commentInfo.subforum+"/"+commentInfo.topic+"/"+commentInfo.commentId+"/"+text,
		    });
			
			topicClicked(commentInfo.subforum,commentInfo.topic)			
		
	}
	else if( commentInfo.operation == "editCommentOnComment" )
	{
		var text=$("#commentTextInput").val();
		$.ajax({
			type:"post",
	        url:subforumService +"updateComment/"+commentInfo.subforum+"/"+commentInfo.topic+"/"+commentInfo.commentId+"/"+text
	    });
		
		topicClicked(commentInfo.subforum,commentInfo.topic)
	}

});

$('#commentTopic').click(function (event){
	
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	if( u != null)
    	{
    		commentInfo.operation="addCommentToTopic";
			
    	}else{
    		alert("Need to login in order to comment");
    		$('#comment-modal').modal('toggle');
    	}
    }); 
	
});



$('#DeleteTopicButton').click(function (event){
	
	event.preventDefault();
	
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	if( u != null)
    	{
    		var arr = $('#TopicName').text().split('-');
     		var topic = arr[0];
     		var subforum= arr[1];
     		
    		if(u.role == "ADMIN" || u.role=="MODERATOR" )
			{
	    		
    			$.ajax({
					type:"delete",
			        url:subforumService +"deleteTopic/"+subforum+"/"+topic,
			    }).then(function (){
			    	alert("Topic Deleted")
			    });
    			displaySubforum(subforum)
    			$('#dispalyTopicWS').hide();
			}
    		else {
    			$.ajax({
	     			type:"get",
	     	        url:subforumService+"getTopic/"+subforum+"/"+topic,
	     	    }).then(function (t){
	     		
	     	    	if(t.author.username == u.username){
	     	    		
			     		$.ajax({
							type:"delete",
					        url:subforumService +"deleteTopic/"+subforum+"/"+topic,
					    }).then(function (){
					    	alert("Topic Deleted")
					    });
			     		
			     		displaySubforum(subforum)
			     		$('#dispalyTopicWS').hide();
	     	    	}
	     	    	else 
	     	    		alert("You have no right to delete this topic")
	     	    });
    		}
			
    	}else{
    		alert("Need to login in order to delete topic");

    	}
    }); 
	
});


$('#saveTopicButton').click(function (event){
	
	event.preventDefault();
	
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	
    	if( u != null)
    	{
    		var arr = $('#TopicName').text().split('-');
     		var topic = arr[0];
     		var subforum= arr[1];
     		
     		$.ajax({
				type:"post",
		        url:subforumService +"saveTopic/"+subforum+"/"+topic,
		    }).then(function (data){
		    	alert(data)
		    });
     		checkUser();
 
			
    	}else{
    		alert("Need to login in order to save topic");

    	}
    }); 
	
});




$("#topicInputType").change(function() {
var selected = $('#topicInputType option:selected').text()
	
	if(selected == "Text")
	{
		$('#textContentDiv').show();
		$('#linkContentDiv').hide();
		$('#imageContentDiv').hide();
	}
	else if(selected == "Link")
	{
		$('#textContentDiv').hide();
		$('#linkContentDiv').show();
		$('#imageContentDiv').hide();
	}
	else if(selected == "Image")
	{
		$('#textContentDiv').hide();
		$('#linkContentDiv').hide();
		$('#imageContentDiv').show();
	}
      
});




$('#subformManagment').click(function (event){

	event.preventDefault();
	
	
	 $.ajax({
			type:"get",
	        url:subforumService+"getSubforums"
	    }).then(function (subforums){
	    	
	    	$('#subforumMenageTable').empty();
	    	
	    	
	    	for(var i=0; i < subforums.length ; i++){

	    		var tableRow =  '<tr>' + 
	    		 '<td>' +
	             subforums[i].name +
	             '</td>' +
	    
	             '<td>' +
	             subforums[i].responsibleModerator.username +
	             '</td>' +
	             
	             '<td>' +
	             '<a href="" class="deleteSubf">Delete</a>'+
	             '</td>' +
	             
	             '</tr>';

				$('#subforumMenageTable').append(tableRow);
	    	}
	    	
	    	$('.deleteSubf').click(function () {
	            var name = $(this).closest('tr').find('td:first-child').text();
	            $.ajax({
	            	method : "post",
	            	url: subforumService + 'delete/' + name
	            }).then(function (message) {
	                alert(message);
	                loadUserList();
	            });
	        });
	    });
	
	 showWS('SubforumManagmentWS')
	
	
	
	
	
});


$('#addSubforumForm').submit(function (event){

	event.preventDefault();
	$("#addSubforumWS").show();
	var form = $("form#addSubforumForm");
	 $.ajax({
	        type: form.attr('method'),
	        url: form.attr('action'),
	        data: form.serialize(),
	        success: function (data) {
	            
	            alert(data);
	            checkUser();
	            
        
	        },
	        error: function (data) {
	            alert('An error occurred.');
	        },
	    });
	
});


$('#addSubforum').click(function (event){

	event.preventDefault();
	showWS("addSubforumWS");
	
	
	
	});
		
	

$("form#loginForm").submit(function( event ) {
	
	var username = $("#username").val();
	var password = $("#password").val();
	user = null;
	
	if ( password == ''|| username == '') {
	alert("Please fill all fields !");
	event.preventDefault();
	} 
	else {
		
		var form = $("form#loginForm");
	    event.preventDefault();
	    $.ajax({
	        type: form.attr('method'),
	        url: form.attr('action'),
	        data: form.serialize(),
	        success: function (data) {
	        	alert(data);
	            if(data == "Login successuful" || data == "User already logdin!")
	            {
	            	//$('#LogedIn').show();
	            	//$('#notLogedIn').hide();
	            	checkUser();
	            }
	            $('.modal').modal('hide');
	            $('#logedInAs').text("Logded in as : "+ $('#username').val());
	            
	        },
	        error: function (data) {
	            alert('An error occurred.');
	        },
	    });

		
	}
});

$('#logOut').click(function (event){

	$.ajax({
		type:"post",
        url:loginService+"logout"
    }).then(function (message) {
    	checkUser();
	    $('#logedInAs').text("Logded in as : "+ $('#username').val());
        alert(message);
        refresh();
    });
});

$('#userManagment').click(function (event){

	event.preventDefault();
	showWS("userManagmentWS");
	
	$("#pn2").hide();
	
	$.ajax({
        url: loginService + "users"
    }).then(function (users) {

    	$('#usersTable').empty();
        // table header
        var tableRow = '<tr>' +
            '<th>username</th>' +
            '<th>role</th>' +
            '<th></th>' +
            '<th></th>' +
            '<th></th>' +
            '</tr>';

        $('#usersTable').append(tableRow);

    	
    		
        users.forEach(function (user) {
            var tableRow = '<tr>' +
                '<td>' + user.username + '</td>' +
                '<td>' + user.role + '</td>' +
                '<td><a class="setRoleAdmin" href="#">Sat as asministrator</a></td>' +
                '<td><a class="setRoleModerator" href="#">Set as moderator</a></td>' +
                '<td><a class="setRoleUser" href="#">Sat as user</a></td>' +
                '</tr>';

            $('#usersTable').append(tableRow);
        });

        $('.setRoleAdmin').click(function () {
            var username = $(this).closest('tr').find('td:first-child').text();
            $.ajax({
            	url: loginService + 'update/admin/' + username
            }).then(function (message) {
                alert(message);
                $("#userManagment").trigger('click');
            });
        });

        $('.setRoleModerator').click(function () {
            var username = $(this).closest('tr').find('td:first-child').text();
            $.ajax({
                url: loginService + 'update/moderator/' + username
            }).then(function (message) {
                alert(message);
                $("#userManagment").trigger('click');
            });
        });

        $('.setRoleUser').click(function () {
            var username = $(this).closest('tr').find('td:first-child').text();
            $.ajax({
                url: loginService + 'update/user/' + username
            }).then(function (message) {
                alert(message);
                $("#userManagment").trigger('click');
            });
        });

    });
	
});

});
		
	

function setIndexForUser(user,loadWS)
{
	if(user == null)
	{
		$('#LogedIn').hide();
	    $('#notLogedIn').show()
	    $('#fullowedSubforumsMeni').hide();
	}
	else 
	{
		$('#LogedIn').show();
	    $('#notLogedIn').hide()
	    $('#savedEntities').show();
	    $('#fullowedSubforumsMeni').show();
	    if(user.role == "ADMIN")
    	{
    		$('#adminOptions').show();
    		$('#moderatorOptions').show();
    		$('#reportOptions').show();
    	}
	    	
	    else if(user.role == "MODERATOR")
    	{
	    	$('#moderatorOptions').show();
	    	$('#reportOptions').show();
    	} 
	    
	    $('#accountInfo').show();
	    
	    $('#logedInAs').text("Logded in as : "+ user.username);
	    if( user.role== "ADMIN")
	    {
	    	$('h3#role').text("Role: Admin");
	    }
	    else if( user.role == "USER")
	    {
	    	$('h3#role').text("Role: User");
	    }
	    else if( user.role == "MODERATOR")
	    {
	    	$('h3#role').text("Role: Moderator");
	    }
	    
	  //FOLLOWED SUBFORUMS
		$('#followedSubformsTable').empty();
		
		var subforums = user.followedSubforums;
		
		for(var i=0; i < subforums.length ; i++){

			var tableRow =  '<tr>' + '<td>' +
	        '<span class="glyphicon glyphicon-file text-primary " ></span><a href="" class="subformeClass">'+subforums[i]+'</a>'+
	        '</tr>'+'</td>';

			$('#followedSubformsTable').append(tableRow);
		}
		
		$('.subformeClass').click(function (event){

			event.preventDefault();
			var name = $(this).closest('tr').find('td:first-child').text();
			
			displaySubforum(name);
		});
		
		
		//WELLCOME PAGE
		
		if(loadWS){
			var table  =$("#welcomeTopicsTable")
	     	table.empty();
			
			var ii =0;
			for(var i=0; i < user.followedSubforums.length;i++){
				
				
				$.ajax({
			     	method : "get",
			     	url: subforumService + 'getSubforum/' + user.followedSubforums[i]
			     }).then(function (subforum) {
			    	 	if(subforum != undefined){
							topics=subforum.topics;
							
					     	topics.forEach(function (topic){
									var tableRow ='<tr>'+
					         	'<td id="'+user.followedSubforums[ii]+'"><a href="" class="topicClicked" ><h1 id="'+user.followedSubforums[ii]+'">'+topic.name+'</h1></a></td>'+
					            '</tr>';
									table.append(tableRow);
									
									
							             
							        });
					     	
						     		$('.topicClicked').click(function (event) {
						            event.preventDefault();
						     		var topic = $(this).closest('tr').find('td:first-child').text();
						            var subforum = $(this).closest('tr').find('td:first-child').attr('id');
						            
						            topicClicked(subforum,topic);
						            
						            showWS('dispalyTopicWS')
						     	});
			    	 	}
			    	 	
				     	ii++;	     	
				     	
		     	});
				
		     	
			}
			
			showWS("welcomeWS")
		}
     	
		//MESSAGES
    	var unread=0;
    	
    	$('#inboxContainer').empty();
    	
    	$.each(user.messages,function(index,message){
    		
    		if(!message.seen)
    			unread++;
    		var row='<div class="row well">'+
			        '<div class="col" style="widtd:200px">'+
			        message.content+
			        '</div>'+
			        '<div class="col">'+
			        	'<div style="color:blue">Sender:'+message.sender+'</div>'+
			        '</div>'+
			        '<div class="col">'+
			        '<a href="" class="replyClass" name="'+message.sender+'">Reply</a>'+
			        '</div>'+
			   '</div>';
    			
    		
    			
    		$('#inboxContainer').append(row);
    	})
    	
    	$('.replyClass').click(function(event){
			event.preventDefault();
			var username=$(this).closest('a').attr("name");
			$('#messageText').text("");
			$('#usernameSend').val(username);
			$('#sendMessage-modal').modal('toggle');
		})
    	
    	$('#unreadMessages').text(unread);
     	
     	
		
		
	}
	
	
	
	
	
	$.ajax({
		type:"get",
        url:subforumService+"getAllSubforums"
    }).then(function (subforums){
    	
    	$('#subformsTable').empty();
    	
    	
    	for(var i=0; i < subforums.length ; i++){

    		var tableRow =  '<tr>' + '<td>' +
            '<span class="glyphicon glyphicon-file text-primary " ></span><a href="" class="subformeClass">'+subforums[i].name+'</a>'+
            '</tr>'+'</td>';
 
			$('#subformsTable').append(tableRow);
    	}
    	
    	$('.subformeClass').click(function (event){

    		event.preventDefault();
    		var name = $(this).closest('tr').find('td:first-child').text();
    		
    		displaySubforum(name);
    	});
    	
    	
    	if(user != null){
    		
    		//COMMENTS
    		
    		$('#savedCommentsTable').empty();
    		
    		$('#savedCommentsTable').append('<tr><th><h5>Saved Comments:</h5></th></tr>');   
	    	for(var i=0; i< user.savedComments.length; i++)
			{
	    		var commentId = user.savedComments[i];
	    		$.ajax({
	    			type:"get",
	    	        //url:subforumService+"getTopic/"+subforum+"/"+topic,
	    			url:subforumService+"getComment/"+commentId,
	    	    }).then(function (comment){
	    	    	
		    		if(comment != undefined){
		    			if(!comment.deleted){
				    		var name = comment.parentTopic+'-'+comment.parentSubforum;
				    		var textOfComment = comment.text.substring(0,8)+"...( "+comment.parentTopic+" )";
				    		var tableRow =  '<tr>' + '<td>' +
				            '<a href="" class="savedCommentClass" name="'+name+'">'+ textOfComment+'</a>'+
				            '</tr>'+'</td>';
				 
							$('#savedCommentsTable').append(tableRow);
							
							$('.savedCommentClass').click(function (event){
								
					    		event.preventDefault();
					    		var name = $(this).closest('tr').find('td:first-child').find('a:first-child').attr("name");
					    		
					    		var arr = name.split('-');
					     		var topic = arr[0];
					     		var subforum= arr[1];
					    		
					     		topicClicked(subforum,topic);
					     		
					     		
					     		showWS('dispalyTopicWS')
					     		
					    	});
		    			}
		    		}
		    			
				 });
			}
	    	 
	    	
	    	
	    	//TOPICS
	    	$('#savedTopicsTable').empty();
    		
    		$('#savedTopicsTable').append('<tr><th><h5>Saved Topics:</h5></th></tr>');   
	    	for(var i=0; i< user.savedTopics.length; i++)
			{
	    		
	    		var topicId= user.savedTopics[i];
	    		
	    		$.ajax({
	    			type:"get",
	    	        //url:subforumService+"getTopic/"+subforum+"/"+topic,
	    			url:subforumService+"getTopic/"+topicId,
	    	    }).then(function (t){
	    	    	if(t != undefined )
	    	    	{
			    		var name = t.name + "-"+ t.parentSubforumName;
			    		var textOfTopic = t.name+"...( "+t.parentSubforumName+" )";
			    		var tableRow =  '<tr>' + '<td>' +
			            '<a href="" class="savedTopicClass" name="'+t.topicId+'">'+ textOfTopic+'</a>'+
			            '</tr>'+'</td>';
			 
						$('#savedTopicsTable').append(tableRow);
					
						$('.savedTopicClass').click(function (event){
							
				    		event.preventDefault();
				    		var topicId = $(this).closest('tr').find('td:first-child').find('a:first-child').attr("name");
				    		
				    		var name= $('#TopicName').text();
				    		var arr = name.split('-');
				     		var topic = "";
				     		var subforum= "";
				    		
				     		
				     		topicClicked(subforum,topic);
				     		
				    		showWS('dispalyTopicWS')
				     
				     		
				    	});
	    	    	}
				 });
			}
	    	 
	    	
	    	
	    	
	   
    	}
    });	
	

}


function checkUser(loadWS){
	
	$.ajax({
		type:"get",
        url:loginService+"getUser"
    }).then(function (u) {
    	setIndexForUser(u,loadWS);

    }); 
}

function displaySubforum(name){
	
        loadTopics(name);
        
        showWS('DispalySubforumWS')
}

function showWS(ws)
{
	var elements = $("[id*="+"WS"+"]")
	$.each(elements,function(index,element){

		if(element.id == ws)
		{
			$(element).show();
		}else
			$(element).hide();	
	
	})
}


function refresh() {
    location.reload(true);
}