// Comment out as appropriate
var domain = "";

var leftMargin = 80;
var width = 1280;
var height = 720;
var timelineOffset = 1100;  // how far down the page the time starts
var timelineScale = 280;    // how many pixel for each year (needs expanding)
var scroll = 0;
var scrollLimit = -2000;

var layer1Speed = 0.8;
var layer2Speed = 0.6;
var layer3Speed = 0.4;

var eventCount = 0;
var eventWidth = 800;
var eventHeight = 60;
var eventHGap = 200;
var eventVGap = 40;

var isDragging = false;
var prev;

var currentCommentEventID = 0;

var getJSON = function(url, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open("get", url, true);
    xhr.responseType = "json";
    xhr.onload = function() {
      var status = xhr.status;
      if (status == 200) {
        callback(null, xhr.response);
      } else {
        callback(status);
      }
    };
    xhr.send();
};

var putJSON = function(url, jsonCommentData, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open("put", url, true);
    //xhr.responseType = "json";
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function() {
      var status = xhr.status;
      if (status == 200) {
        callback(null, xhr.response);
      } else {
        callback(status);
      }
    };
    xhr.send(jsonCommentData);
};

// Initialise comments modal
var modal = new tingle.modal({
  //footer: true,
  //stickyFooter: false,
  cssClass: ['custom-class-1', 'custom-class-2'],
  onOpen: function() {
      console.log('modal open');
  },
  onClose: function() {
      console.log('modal closed');
  }
});

modal.setContent('<form class=\"comment-form\" action=\"\" method=\"get\" onsubmit=\"return handleComment();\">Your name.<input name=\"name\" maxlength=\"50\" id=\"name_field\" type=\"text\">Your email.<br><input name=\"email\" maxlength=\"50\" id=\"email_field\" type=\"text\">Your comment. <br><input name=\"comment\" maxlength=\"160\" id=\"comment_field\" type=\"text\"><br><input type=\"submit\" value=\"Comment\" ></form>');

var renderer = PIXI.autoDetectRenderer(width, height, { autoResize:true, antialias:true});
renderer.backgroundColor = 0x0060BC;
document.body.appendChild(renderer.view);
document.addEventListener("wheel", mouseWheelHandler, false);
window.addEventListener('resize', resizeHandler);

// create the root of the stage graph
var stage = new PIXI.Container();
stage.interactive = true;
addDragScroll();

// container for timeline elements. (events, comments)
var timeline = new PIXI.Container();
stage.addChild(timeline);
timeline.x = 0;
timeline.y = 1000;
timeline.interactive = true;

// Load event data from DM plugin
getJSON(domain + "/gamechangers/v1/events",
function(err, data) {
  if (err != null) {
    console.log("something no worky");
  } else {
    // sort data by date (move to backend?)
    data.sort(function(a, b) {
      return parseFloat(a.from) - parseFloat(b.from);
    });

    for (var i = 0; i < data.length; i++){
      console.log(name + ' - ' + data[i].name);
      console.log(name + ' - ' + data[i].id);
      //console.log(name + ' - ' + data[i].address);
      //console.log(name + ' - ' + data[i].notes);
      console.log(name + ' - ' + data[i].from);
      var d = new Date(data[i].from);
      console.log('human readable date - ' + d.getFullYear() );
      //console.log(name + ' - ' + data[i].to);
      if( typeof data[i].from === 'undefined' || data[i].from === null ){
        console.log('missing from date for object ' + i);
      }
      if( typeof data[i].to === 'undefined' || data[i].to === null ){
        data[i].to = -1;
      }
      addEvent(300,i * timelineScale, data[i].name, data[i].id ,data[i].notes, 0, data[i].from ,timeline);
    }
  }
  // Change this to find the first and last date, or fix to specific date range?
  scrollLimit = (-Math.abs((data.length*timelineScale) + timelineOffset));
});

// PUT comment test
/*
putJSON("http://localhost:8080/gamechangers/v1/comment",
JSON.stringify({
    name: 'John Smith',
    email: 'john@smith.com',
    notes: 'I think, blah blah...',
    commentedItemId: 4801
}),
function(err, data) {
  if (err != null) {
    console.log("something no worky");
  } else {
    for (var i = 0; i < data.length; i++){
      console.log('It worked...');
      console.log('...at least partly');
    }
  }
});
*/

// Nasty hardcoded layout of images and text
var ctaTexture = PIXI.Texture.fromImage("img/cta.png");
var cta = new PIXI.Sprite(ctaTexture);
cta.position.x = 800;
cta.position.y = 200;
stage.addChild(cta);

var logoTexture = PIXI.Texture.fromImage("img/logo.png");
var logo = new PIXI.Sprite(logoTexture);
logo.position.x = leftMargin;
logo.position.y = 100;
stage.addChild(logo);

// interactivity and modal test
cta.interactive = true;

cta.mouseover = function(mouseData){
   cta.alpha = 0.75;
};

cta.mouseout = function(mouseData){
   cta.alpha = 1;
};

cta.click = function(mouseData){
   console.log("open modal");
   openModal();
};

var layer1 = new PIXI.Container();
stage.addChild(layer1);
layer1.x = 0;
layer1.y = 0;

var layer2 = new PIXI.Container();
stage.addChild(layer2);
layer2.x = 0;
layer2.y = 0;

var layer3 = new PIXI.Container();
stage.addChild(layer3);
layer3.x = 0;
layer3.y = 0;

// hardcoded text creation and placement
var textContainer = new PIXI.Container();
stage.addChild(textContainer);
textContainer.x = leftMargin;
textContainer.y = 360;

// text styles
var styleHeading = {
    fontFamily : 'Helvetica',
    fontSize : '24px',
    fontStyle : 'normal',
    fontWeight : 'normal',
    fill : '#FFFFFF'
};

var styleNormal = {
    fontFamily : 'Helvetica',
    fontSize : '18px',
    fontStyle : 'normal',
    fontWeight : 'normal',
    fill : '#FFFFFF',
    wordWrap : true,
    wordWrapWidth : 440
};

var styleSmall = {
    fontFamily : 'Helvetica',
    fontSize : '14px',
    fontStyle : 'normal',
    fontWeight : 'normal',
    fill : '#FFFFFF',
    wordWrap : true,
    wordWrapWidth : 440
};

var styleComments = {
    fontFamily : 'Courier',
    fontSize : '18px',
    fontStyle : 'normal',
    fontWeight : 'normal',
    fill : '#FFFFFF',
    wordWrap : true,
    wordWrapWidth : 440
};

// Text elements
var title1 = new PIXI.Text('Hi there!',styleHeading);
title1.x = 0;
title1.y = 0;
textContainer.addChild(title1);

var text1 = new PIXI.Text('This is the page is for gathering info for the timeline of Aalto University and it’s predecessors events, inventions, individuals and impact on the Finnish society. Whether you are a student, professor, alumni or anyone who has some insight to share reagarding the history of Aalto, you are most welcome to contribute. This is how it works:',styleNormal);
text1.x = 0;
text1.y = 30;
textContainer.addChild(text1);

var title2 = new PIXI.Text('Comment',styleHeading);
title2.x = 0;
title2.y = 220;
textContainer.addChild(title2);

var text2 = new PIXI.Text('Scroll down the page and select an item you want to comment. You have 160 characters. Point of view for the comments should be on the impact on society.',styleNormal);
text2.x = 0;
text2.y = 250;
textContainer.addChild(text2);

var title3 = new PIXI.Text('Propose',styleHeading);
title3.x = 0;
title3.y = 350;
textContainer.addChild(title3);

var text3 = new PIXI.Text('This is the beginning of a diverse and multilayered story of Aalto University. To get going, we have selected 100 events. Propose more, be specific and keep the focus on the impact on society.',styleNormal);
text3.x = 0;
text3.y = 380;
textContainer.addChild(text3);

var title4 = new PIXI.Text('SMALLPRINT :)',styleSmall);
title4.x = 0;
title4.y = 500;
textContainer.addChild(title4);

var text4 = new PIXI.Text('The feedback you give is subject to be edited for forming larger blocks of content or used as it is, as an anecdote. Should you be directly quoted, our editorial team will be in contact. Please make sure you write correct contact details.',styleSmall);
text4.x = 0;
text4.y = 530;
textContainer.addChild(text4);

var whitelines = new PIXI.Graphics();

whitelines.lineStyle(1, 0xffffff, 1);
whitelines.moveTo(0,525);
whitelines.lineTo(420, 525);
whitelines.moveTo(0,610);
whitelines.lineTo(420, 610);

textContainer.addChild(whitelines);

// start animating
resizeHandler();

animate();

function init() {
  // init stuff here maybe
};

function animate() {
    renderer.render(stage);
    requestAnimationFrame(animate);
};

// Handle scrolling based on mouse wheel. Grab scroll with also be needed.
// with touch support
function mouseWheelHandler(event) {
  scroll -= event.deltaY;

  if (scroll > 0) {
    scroll = 0;
  }
  if (scroll < scrollLimit) {
   scroll = scrollLimit;
  }

  stage.y = scroll;
  layer1.y = scroll * layer1Speed;
  layer2.y = scroll * layer2Speed;
  layer3.y = scroll * layer3Speed;
};

// resize the rendered when the browser is resized
function resizeHandler() {
  console.log('resize event ' + window.innerHeight);
  renderer.view.style.width = width;
  renderer.view.style.height = window.innerHeight;
  renderer.resize(width,window.innerHeight);
};

// draws a new even to the timeline and increments event counter
function addEvent(x, y, title, id, text, depth, year, layer) {

  // Draw event container
  var gfx = new PIXI.Graphics();
  var xpos = eventHGap;
  var ypos = eventCount*(eventVGap + eventHeight);
/*
  gfx.lineStyle(2, 0xffd900, 1);
  gfx.drawRect(xpos, ypos, eventWidth, eventHeight);
  gfx.moveTo(xpos,ypos+40);
  gfx.lineTo(xpos+eventWidth, ypos+40);
*/
  gfx.lineStyle(0);
  gfx.beginFill(0xFFFFFF, 0.8);
  gfx.drawCircle(xpos + 370, ypos + 20, 10);
  gfx.endFill();
  layer.addChild(gfx);

  // Draw event text
  var d = new Date(year);
  var eventDate = new PIXI.Text(d.getFullYear(),styleHeading);
  eventDate.x = xpos + 280;
  eventDate.y = ypos + 5;
  layer.addChild(eventDate);

  // Draw event text
  var eventTitle = new PIXI.Text(title,styleHeading);
  eventTitle.x = xpos + 420;
  eventTitle.y = ypos + 5;
  layer.addChild(eventTitle);

  // add commment button
  // Nasty hardcoded layout of images and text
  var commentButtonTexture = PIXI.Texture.fromImage("img/comment.png");
  var commentButton = new PIXI.Sprite(commentButtonTexture);
  commentButton.position.x = xpos + 170;
  commentButton.position.y = ypos + eventHeight - 20;
  //commentButton.width = eventWidth;
  //commentButton.height = 10;
  layer.addChild(commentButton);

  // interactivity and modal test
  commentButton.interactive = true;

  commentButton.mouseover = function(mouseData){
     commentButton.alpha = 0.75;
  };

  commentButton.mouseout = function(mouseData){
     commentButton.alpha = 1;
  };

  commentButton.click = function(mouseData){
     console.log("open comment " + id);
     currentCommentEventID = id;
     openCommentModal();
  };

  eventCount ++;
};

function handleComment() {
  console.log('handle comment button pressed for :' + currentCommentEventID);

  var n = document.getElementById('name_field').value;
  var e = document.getElementById('email_field').value;
  var c = document.getElementById('comment_field').value;

  if (!n){
    window.alert("Please enter your name.");
    return false;
  }

  if (!e){
    window.alert("Please enter your email.");
    return false;
  }

  if (!c){
    window.alert("Please enter a comment.");
    return false;
  }

  putJSON(domain + "/gamechangers/v1/comment",
  JSON.stringify({
      name: n,
      email: e,
      notes: c,
      commentedItemId: currentCommentEventID
  }),
  function(err, data) {
    if (err != null) {
      window.alert("Unfortunately there was an error submitting your comment. Please try again.");
    } else {
      window.alert("Thank you for your comment");
    }
  });

  modal.close();
  return false;
};

// opens a modal comment form.
function openCommentModal() {
    modal.open();
};

// drag scroll functions
function addDragScroll() {
    stage.on('mousedown', dragStart);
    stage.on('touchstart', dragStart);
    // events for drag end
    stage.on('mouseup', dragEnd);
    stage.on('mouseupoutside', dragEnd);
    stage.on('touchend', dragEnd);
    stage.on('touchendoutside', dragEnd);
    // events for drag move
    stage.on('mousemove', dragMove);
    stage.on('touchmove', dragMove);
  };

function dragStart(moveData) {

    prev = moveData.data.getLocalPosition(this).y;
    isDragging = true;
    console.log("mouse down - drag begins : " + moveData.data.getLocalPosition(this).y );
  };

  function dragMove(moveData) {
    if (!isDragging) {
      return;
    }
    var dy = moveData.data.getLocalPosition(this).y - prev;
    stage.y += dy;
    prev = moveData.data.getLocalPosition(this).y;
    console.log("mouse move : y = " + moveData.data.getLocalPosition(this).y);
  };

  function dragEnd(moveDate) {
    isDragging = false;
    console.log("mouse up - drag ends");
  }
