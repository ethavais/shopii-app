// wwwroot/js/chat.js

// Khởi tạo khi trang tải
document.addEventListener('DOMContentLoaded', function () {
    var messagesContainer = document.getElementById('messages-container');
    if (messagesContainer) {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    var messageInput = document.querySelector('.message-input');
    if (messageInput) {
        messageInput.addEventListener('input', function () {
            this.style.height = 'auto';
            this.style.height = (this.scrollHeight) + 'px';
        });
    }

    // Bắt đầu kiểm tra tin nhắn mới định kỳ
    startPollingMessages();
});

// Khởi tạo SignalR
var connection = new signalR.HubConnectionBuilder()
    .withUrl("/chatHub")
    .build();

// Xử lý tin nhắn mới từ SignalR
connection.on("ReceiveMessage", function (userId, message, sender) {
    console.log(`Received message via SignalR: ${userId}, ${message}, ${sender}`);
    var currentUserId = document.getElementById('userId') ? document.getElementById('userId').value : null;
    if (userId === currentUserId) {
        displayMessage(userId, message, sender);
    }
    updateContactList(userId, message);
});

// Xử lý user mới
connection.on("NewUser", function (userId) {
    updateContactList(userId, "Chưa có tin nhắn");
});

// Kết nối SignalR
connection.start()
    .then(() => console.log("SignalR connected successfully"))
    .catch(function (err) {
        console.error("SignalR connection error: ", err.toString());
    });

// Gửi tin nhắn qua POST
function sendMessageViaPost(event) {
    event.preventDefault();
    var userId = document.getElementById('userId').value;
    var message = document.getElementById('messageContent').value.trim();

    if (message) {
        fetch('/', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `userId=${encodeURIComponent(userId)}&message=${encodeURIComponent(message)}&__RequestVerificationToken=${document.querySelector('input[name="__RequestVerificationToken"]').value}`
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    document.getElementById('messageContent').value = '';
                    // Hiển thị tin nhắn ngay lập tức cho người gửi
                    displayMessage(userId, message, "admin");
                }
            })
            .catch(err => console.error("POST error: ", err));
    }
}

// Gửi tin nhắn qua SignalR
function sendMessage() {
    var userId = document.getElementById('userId').value;
    var message = document.getElementById('messageContent').value.trim();

    if (message) {
        console.log(`Sending message: ${userId}, ${message}`);
        connection.invoke("SendMessage", userId, message)
            .then(() => {
                console.log("SendMessage invoked successfully");
                document.getElementById('messageContent').value = '';
                // Hiển thị tin nhắn ngay lập tức cho người gửi
                displayMessage(userId, message, "admin");
            })
            .catch(err => console.error("SendMessage error: ", err));
    } else {
        console.log("Message is empty, not sending");
    }
}

// Hàm hiển thị tin nhắn
function displayMessage(userId, message, sender) {
    var messagesContainer = document.getElementById('messages-container');
    if (messagesContainer) {
        var messageDiv = document.createElement('div');
        messageDiv.className = 'message ' + (sender === "admin" ? "user" : "other");
        var contentDiv = document.createElement('div');
        contentDiv.className = 'message-content';
        contentDiv.textContent = message;
        var metaDiv = document.createElement('div');
        metaDiv.className = 'message-meta';
        var timeSpan = document.createElement('span');
        timeSpan.className = 'message-time';
        timeSpan.textContent = new Date().toLocaleTimeString('en-US', { hour12: false });
        metaDiv.appendChild(timeSpan);
        if (sender === "admin") {
            var statusSpan = document.createElement('span');
            statusSpan.className = 'message-status';
            var icon = document.createElement('i');
            icon.className = 'bi bi-check';
            statusSpan.appendChild(icon);
            metaDiv.appendChild(statusSpan);
        }
        messageDiv.appendChild(contentDiv);
        messageDiv.appendChild(metaDiv);
        messagesContainer.appendChild(messageDiv);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }
}

// Cập nhật danh sách user
function updateContactList(userId, lastMessage) {
    var contactsList = document.getElementById('contacts-list');
    var existingContact = contactsList.querySelector(`[data-user-id="${userId}"]`);

    if (!existingContact) {
        var contactDiv = document.createElement('div');
        contactDiv.className = 'contact-item';
        contactDiv.setAttribute('data-user-id', userId);
        contactDiv.onclick = function () { location.href = '?userId=' + userId; };

        var avatarDiv = document.createElement('div');
        avatarDiv.className = 'avatar';
        avatarDiv.textContent = userId[0];

        var infoDiv = document.createElement('div');
        infoDiv.className = 'contact-info';

        var nameDiv = document.createElement('div');
        nameDiv.className = 'contact-name';
        nameDiv.textContent = userId;

        var messageDiv = document.createElement('div');
        messageDiv.className = 'contact-last-message';
        messageDiv.textContent = lastMessage;

        infoDiv.appendChild(nameDiv);
        infoDiv.appendChild(messageDiv);

        var metaDiv = document.createElement('div');
        metaDiv.className = 'contact-meta';

        var timeDiv = document.createElement('div');
        timeDiv.className = 'contact-time';
        timeDiv.textContent = new Date().toLocaleTimeString('en-US', { hour12: false });

        metaDiv.appendChild(timeDiv);

        contactDiv.appendChild(avatarDiv);
        contactDiv.appendChild(infoDiv);
        contactDiv.appendChild(metaDiv);
        contactsList.appendChild(contactDiv);
    } else {
        var lastMessageDiv = existingContact.querySelector('.contact-last-message');
        lastMessageDiv.textContent = lastMessage;
        var timeDiv = existingContact.querySelector('.contact-time');
        timeDiv.textContent = new Date().toLocaleTimeString('en-US', { hour12: false });
    }
}

// Polling để kiểm tra tin nhắn mới
let lastMessageId = null;
function startPollingMessages() {
    var currentUserId = document.getElementById('userId') ? document.getElementById('userId').value : null;
    if (!currentUserId) return;

    setInterval(() => {
        fetch(`/api/chat/latest?userId=${encodeURIComponent(currentUserId)}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data && data.id && data.id !== lastMessageId) {
                    console.log(`New message detected via polling: ${data.userId}, ${data.text}, ${data.from}`);
                    lastMessageId = data.id;
                    displayMessage(data.userId, data.text, data.from);
                    updateContactList(data.userId, data.text);
                }
            })
            .catch(err => console.error("Polling error: ", err));
    }, 5000); // Kiểm tra mỗi 5 giây
}