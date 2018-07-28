'use strict';
var Realm = require('realm'); 
// the URL to the Realm Object Server
var SERVER_URL = '//127.0.0.1:9080';
// The regular expression you provide restricts the observed Realm files to only the subset you
// are actually interested in. This is done in a separate step to avoid the cost
// of computing the fine-grained change set if it's not necessary.
//var NOTIFIER_PATH = '^/([^/]+)/private$';
var NOTIFIER_PATH = '^/.*/ROS5$';
//declare admin user 
let adminUser = undefined
// The handleChange callback is called for every observed Realm file whenever it
// has changes. It is called with a change event which contains the path, the Realm,
// a version of the Realm from before the change, and indexes indication all objects
// which were added, deleted, or modified in this change
var handleChange = async function (changeEvent) {
  // Extract the user ID from the virtual path, assuming that we're using
  // a filter which only subscribes us to updates of user-scoped Realms.
  //var matches = changeEvent.path.match("^/([^/]+)/([^/]+)$");
  //var userId = matches[1];
  /*var realm = changeEvent.realm;
  var commentAndRatings = realm.objects('CommentAndRating');
  var commentAndRatingIndexes = changeEvent.changes.CommentAndRating.modifications;
  for (let commentAndRatingIndex of commentAndRatingIndexes) {
    var commentAndRating = commentAndRatings[commentAndRatingIndex];
    if (commentAndRating.isValid !== undefined) {
      //var isValid = verifyCouponForUser(commentAndRating, userId);
      // Attention: Writes here will trigger a subsequent notification.
      // Take care that this doesn't cause infinite changes!
      //realm.write(function() {
       // commentAndRating.isValid = isValid;
      //});
      var item = commentAndRating.item;
      // let average = realm.objects('Car').avg('miles');
      var itemRating = realm.objects('CommentAndRating').filtered('item = $', item).avg("rating");
      itemRating = parseFloat(itemRating.toFixed(2));
      console.log("Item name: $, rating: $", item.name, itemRating);
      realm.write(function() {
        item.rating = 10.0;// itemRating;
      });
      //var item = realm.objects('Item').filtered('name = ')
    }
  }*/
  var realm = changeEvent.realm;
  for (var className in changeEvent.changes) {
     var changes = changeEvent.changes[className];
     var objects = realm.objects(className);
	if (className == "OrderPlaced"){
		console.log("Changes in model: OrderPlaced");
		for (let pos of changes.insertions){
  			//console.log("- object modified at position ", pos, " : ", objects[pos]);
			let items = objects[pos].items;
			console.log("Items \n", items);
			for (var i = 0; i < items.length; i++){
				//item.numberTimesOrdered = item.numberTimesOrdered + 1;	
				/*realm.write(function() {
        				item.rating = "9";
      				});*/	
				console.log("---- Item ---- \n", items[0]);	
				realm.write(function() {
        				items[i].numberTimesOrdered = items[i].numberTimesOrdered + 1;
      				});
			}
		}
	}
	if (className == "CommentAndRating"){
     console.log("Changes in model: CommentAndRating");
     for (let pos of changes.modifications){
         console.log("- object modified at position ", pos, " : ", objects[pos]);
	//console.log("- User: -> ", user);
         var item = objects[pos].item; // proveri sa usera;;; Treba ide ovde item
         console.log("Item: --- ", item);
          /*var items2 = realm.objects('Item');
      console.log("- Items: -> ", items2);*/
      var itemRating = realm.objects('CommentAndRating').filtered('item.name = "' + item.name + '"').avg("rating");
      console.log("- ItemRating: -> ", itemRating);
      itemRating = parseFloat(itemRating.toFixed(1));
      var ratingString = itemRating.toString();
      if (!ratingString.includes(".")) {
      	ratingString += ".0";
      	itemRating = ratingString;
      	//itemRating = parseFloat(ratingString);
      }
      console.log("Item name: ", item.name, ", rating: ", itemRating);
      realm.write(function() {
        item.rating = itemRating.toString();
      });
     }
 }
     console.log("");
  }
}
function verifyCouponForUser(commentAndRating, userId) {
    //logic for verifying a coupon's validity
}
// register the event handler callback
async function main() {
try{
    //adminUser = await Realm.Sync.User.login(`http://127.0.0.1:9080`, 'golden', 'golden')
    adminUser = await Realm.Sync.User.login(`http:${SERVER_URL}`, 'realm-admin', '')
    Realm.Sync.addListener(`realm:${SERVER_URL}`, adminUser, NOTIFIER_PATH, 'change', handleChange);
}
catch(ex)
{console.log("ExceptionBre:" + ex.message)}
}
main()
