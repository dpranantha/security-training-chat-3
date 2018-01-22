var ui = null;

/* Models */

var User = function() {
    this.id = null;
    this.username = null;
};

User.prototype.update = function(user) {
    this.id = user.id;
    this.username = user.username;
};

var User_from_id = function(user_id) {
    if(!(user_id in ui.users)) ui.users[user_id] = new User();
    return ui.users[user_id];
};

var Channel = function() {
    this.id = null;
    this.name = null;

    this.messages = [];

    this.users = [];
};

Channel.prototype.update = function(channel) {
    this.id = channel.id;
    this.name = channel.name;

    for(var i = 0; i < channel.messages.length; i++) {
        if(channel.messages[i].id in ui.messages) continue;

        var message = Message_from_id(channel.messages[i].id);
        message.update(channel.messages[i]);

        this.messages.push(message);
    }

    this.users = [];
    for(var i = 0; i < channel.users.length; i++) {
        var user = User_from_id(channel.users[i].id);
        user.update(channel.users[i]);

        this.users.push(user);
    }

};

Channel_from_id = function(channel_id) {
    if(!(channel_id in ui.channels)) ui.channels[channel_id] = new Channel();
    return ui.channels[channel_id];
};

var Message = function() {
    this.id = null;
    this.user = null;
    this.message = null;
    this.date = null;
};

Message.prototype.update = function(message) {
    var user = User_from_id(message.user.id);
    user.update(message.user);

    this.id = message.id;
    this.user = user;
    this.message = message.message;
    this.date = message.date;
};

Message_from_id = function(message_id) {
    if(!(message_id in ui.messages)) ui.messages[message_id] = new Message();
    return ui.messages[message_id];
};


/* API calls */

var api_channel = function(callback_done, callback_fail) {
    $.ajax({
        url: "/channel?generation="+ parseInt(timestamp()),
        headers: {
            "Accept": "application/json"
        }
    })
    .done(callback_done)
    .fail(callback_fail);

    return true;
};

var api_message = function(channel_id, message, callback_done, callback_fail) {
    $.ajax({
        url: "/message",
        type: "POST",
        data: {
            channel: channel_id,
            message: message,
        },
        headers: {
            "Accept": "application/json"
        }
    })
    .done(callback_done)
    .fail(callback_fail);

    return true;
};

/* UI */

var UI = function() {
    this.channels = {};
    this.messages = {};
    this.users = {};
    this.generation = parseInt(timestamp());

    this.reload = null;

    this.active_channel = null
};

UI.prototype.init = function() {
    ui.state = "load";
    ui.draw();
    ui.refresh();
};

UI.prototype.refresh = function() {
    api_channel(function(data, xhr) {
        ui.state = "chat";
        ui.handle_channel_response(data);
        ui.draw();
    }, function(xhr) {
        ui.state = "error";
        ui.draw();
    });
};

UI.prototype.refresh_avatars = function() {
    ui.generation = parseInt(timestamp());

    var generation = "generation=" + parseInt(ui.generation);

    $("ul.channel_content .icon img , div.members img").each(function() {
        $(this).attr(
            "src",
            $(this).attr("src").replace(/generation=\d+/, generation)
        );
    });
};

UI.prototype.draw = function() {
    $("div.container").hide();

    window.clearInterval(ui.reload);

    if(ui.state == "load") {
        $("div.container.load").show();
        $("div#overlay").addClass("active");
    }

    if(ui.state == "error") {
        $("div.container.error").show();
        $("div#overlay").addClass("active");
    }

    if(ui.state == "help") {
        $("div.container.help").show();
        $("div#overlay").addClass("active");
    }

    if(ui.state == "chat") {
        $("div.container.chat").show();
        $("form.message_form input[name='message']").focus();
        $("div#overlay").removeClass("active");

        ui.draw_active_channel();
        ui.draw_channel_list();
        ui.draw_member_list();

        ui.reload = window.setTimeout(ui.refresh, 10000);
    }

};

UI.prototype.draw_active_channel = function() {
    var channel = ui.active_channel;

    var channel_container = $("div.channel");
    var message_container = channel_container.find("ul.channel_content");

    // if we're swapping channels we need to clear our messages box
    if(channel_container.attr("data-id") != channel.id) {
        channel_container.attr("data-id", channel.id);
        message_container.find("li").remove();
    }

    var channel_content = $("ul.channel_content");

    for(var i = 0; i < channel.messages.length; i++) {
        var message = channel.messages[i];
        if(!channel_content.find("li[data-id='"+ parseInt(message.id) + "']").length) {
            var element = $("<li></li>").attr("data-id", message.id).appendTo(channel_content);
            element.addClass("message");
    
            var icon = $("<div></div>").addClass("icon").appendTo(element);
            var avatar = $("<img />").appendTo(icon);
            avatar.attr("src", "/avatar?user_id="+ parseInt(message.user.id) + "&generation=" + parseInt(ui.generation));
    
            var message_element = $("<div></div>").addClass("message").appendTo(element);
    
            var meta = $("<div></div>").addClass("meta").appendTo(message_element);
            var meta_user = $("<div></div>").addClass("user").appendTo(meta);
            var meta_date = $("<div></div>").addClass("date").appendTo(meta);
    
            var text = $("<div></div>").addClass("text").appendTo(message_element);

            meta_user.text(message.user.username);
            meta_date.text(format_timestamp(message.date));

            text.text(message.message);
            var escaped_text = text.html();
            escaped_text  = escaped_text.replace(/:([a-zA-Z0-9-]*):/, "<img class=\"emoji\" src=\"/static/emoji/$1.png\">");
            escaped_text = escaped_text.replace(/([a-z]+:[^ ]*)/, "<a href=\"$1\" target=\"_BLANK\">$1</a>");
            //this is vulnerable since you can put href with javascript function, e.g.
            //javascript:alert(1), so if people click on it, you can take all of his cookies (in a jar :))
            //fix it:
            // regex https?:\/ .... for href (NOT a good solution)
            // open redirect vulnerability: phishing --> verify links, outgoing link dialog (difficult)
            // window.opener vulnerability also phishing --> noopener noreferrer
            text.html(escaped_text);
   
            channel_content.get(0).scrollTop = channel_content.get(0).scrollHeight;
        }
    }

    channel_container.attr("data-generation", timestamp()); 
};

UI.prototype.draw_channel_list = function() {
    var container = $("div.channels");
    var list = container.find("ul.channels_list");

    if(container.attr("data-id") != ui.active_channel.id) {
        container.attr("data-id", ui.active_channel.id);
    }

    list.find("li").remove();

    var channels = sort(object_values(ui.channels), "name");

    for(var i = 0; i < channels.length; i++) {
        var channel = channels[i];
        var element = $("<li></li>").appendTo(list);

        element.attr("data-id", channel.id);
        element.text(channel.name);
        if(channel.id == ui.active_channel.id) {
            element.addClass("active");
        }
        element.addClass("allowed");
        element.on("click", ui.handle_active_channel);
    }
    
    channels = get_channels();
    ui_channels = object_values(ui.channels);
    all_channels = object_keys(channels);
    for(var i = 0; i < all_channels.length; i++) {
        var channel = all_channels[i];
        var found = false;
        for (var j = 0; j < ui_channels.length; j++) {
            if (ui_channels[j].name == channel) {
                found = true;
            }
        }
        if (found) {
            continue;
        }
        var element = $("<li></li>").appendTo(list);
        element.attr("data-id", channels[channel]);
        element.text("(" + channel + ")");
        element.addClass("denied");
    }
};

UI.prototype.draw_member_list = function() {
    var container = $("div.members");
    var list = $("ul.members_list");

    if(container.attr("data-id") != ui.active_channel.id) {
        container.attr("data-id", ui.active_channel.id);
    }

    list.find("li").remove();

    var users = sort(object_values(ui.active_channel.users), "username");

    for(var i = 0; i < users.length; i++) {
        var user = users[i];
        var element = $("<li></li>").appendTo(list);

        var avatar = $("<img />").appendTo(element);
        avatar.attr("src", "/avatar?user_id="+ parseInt(user.id) + "&generation=" + parseInt(ui.generation));

        element.attr("data-id", user.id);


        var span = $("<span></span>").appendTo(element);

        if (user.username == get_username()) {
            span.addClass("active");
        }

        span.text(user.username);
    }
};

UI.prototype.handle_channel_response = function (data) {
    if(!data.length) {
        ui.state = "error";
        ui.draw();
        return false;
    }

    for(var i = 0; i < data.length; i++) {
        var channel = Channel_from_id(data[i].id);
        channel.update(data[i]);
    }

    if(ui.active_channel) {
        var found = false;
        for(var i = 0; i < data.length; i++) {
            if (data[i].id == ui.active_channel.id) {
                found = true;
            }
        }
        if (!found) {
            // login again
            location.reload();
        }
    } else {
        ui.active_channel = ui.channels[
            object_keys(ui.channels)[0]
        ];
    }

    ui.draw();
};

UI.prototype.set_active_channel = function(channel_id) {
    ui.active_channel = ui.channels[channel_id];
    ui.draw();
};

UI.prototype.handle_active_channel = function(event) {
    event.preventDefault();
    ui.set_active_channel($(this).attr("data-id"));
};

UI.prototype.handle_message_form = function(event) {
    event.preventDefault();

    $("form.message_form input").prop("disabled", true);
    $("form.message_form button").prop("disabled", true);

    var message = $(this).find("input[type='text']").val();

    api_message(ui.active_channel.id, message, function(data, xhr) {
        $("form.message_form input").val("");

        $("form.message_form input").prop("disabled", false);
        $("form.message_form button").prop("disabled", false);

        ui.refresh();

        $("form.message_form input[name='message']").focus();
    }, function(xhr) {
        if(xhr.status == 401) {
            location.reload();
        } else {
            $("form.message_form input").prop("disabled", false);
            $("form.message_form button").prop("disabled", false);
            ui.refresh();
        }
    });

    return false;
};

UI.prototype.handle_overlay_click = function(event) {
    event.preventDefault();
    event.stopPropagation();

    if(ui.state == "help") {
        ui.state = "chat";
        ui.draw();
    }
        
    return false;
};

UI.prototype.handle_overlay_key = function(event) {
    event.preventDefault();
    event.stopPropagation();

    if(event.keyCode == 0x1b && (ui.state == "help")) {
        ui.state = "chat";
        ui.draw();
    }
        
    return false;
};

UI.prototype.handle_help_link = function(event) {
    event.preventDefault();

    ui.state = "help";
    ui.draw();

    return false;
};

var init = function() {
    // do a full setup of our user interface, removing loaders etc
    // and start loading our channels
    ui = new UI();

    $("form.message_form").on("submit", ui.handle_message_form);

    $("a.help").on("click", ui.handle_help_link);

    $("div#overlay").on("click", ui.handle_overlay_click);

    $(document).on("keyup", ui.handle_overlay_key);

    ui.init();
};

if(typeof $ == "undefined") {
    var tag = document.createElement("script");
    tag.type = "text/javascript";
    tag.src = "https://www.certifiedsecure.nl/js/jquery/jquery.external.js";
    tag.onload = init;

    document.getElementsByTagName("head")[0].appendChild(tag);
} else {
    $(document).ready(init);
}


/* Helper functions */

var pad = function(value, padding, length) {
    value = value + "";
    padding = padding + "";
    if(value.length >= length) {
        return value;
    } else {
        return padding + (new Array(length - value.length).join(padding)) + value;
    }
}

var sort = function(array, by) {
    return array.concat().sort(sort_by(by));
};

var sort_by = function(property) {
    return function(l, r) {
        if(l[property] < r[property]) return -1;
        if(l[property] > r[property]) return 1;
        return 0;
    };
};

var format_timestamp = function(time) {
    var date = new Date(time);

    return pad(date.getHours(), 0, 2) + ":" +
           pad(date.getMinutes(), 0, 2) + " " +
           pad(date.getDate(), 0, 2) + "-" +
           pad(date.getMonth() + 1, 0, 2) + "-" +
           date.getFullYear();
};

var get_username = function() {
    return $("meta[name='username']").attr("content");
};

var get_channels = function() {
    return JSON.parse($("meta[name='channels']").attr("content"));
};

var object_keys = function(object) {
    var arr = Array();

    for(var key in object) {
        arr.push(key);
    }

    return arr;
};

var object_values = function(object) {
    var arr = Array();
    var keys = object_keys(object);

    for(var i = 0; i < keys.length; i++) {
        arr.push(object[keys[i]]);
    }

    return arr;
};

var timestamp = function() {
    return Date.now ? Date.now() : new Date().getTime();
}
