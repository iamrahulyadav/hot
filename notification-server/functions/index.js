/*
 * Functions SDK : is required to work with firebase functions.
 * Admin SDK : is required to send Notification using functions.
 */

'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


 exports.notify = functions.https.onCall((data, context) => {
    console.log("data is ", data);
    const {userId, message, title} = data;

    console.log("userId is ", userId);

    const deviceToken = admin.database().ref(`/users/${userId}/deviceToken`).once('value');

    deviceToken.then(result => {

        const token_id = result.val();

        console.log("device token_id is ", token_id);

        const payload = {
                data : {
                  title : title,
                  message: message
                }
              };

         console.log("notification payload is: ", payload);

              return admin.messaging().sendToDevice(token_id, payload).then(response => {

                console.log('notification sent to user ', data.userId);

              });
    });
  });
