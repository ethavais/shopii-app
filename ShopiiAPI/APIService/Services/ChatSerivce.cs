using APIService.Models;
using FireSharp.Interfaces;
using FireSharp.Response;
using Microsoft.AspNetCore.SignalR;
using System.Reflection;

namespace APIService.Services
{
    public class ChatService : IChatService
    {
        private readonly IFirebaseClient firebaseClient;
        private readonly IHubContext<ChatHub> _hubContext;
        private const string StoreId = "singleStore";

        public ChatService(IHubContext<ChatHub> hubContext)
        {
            firebaseClient = FirebaseConfigSetup.GetClient();
            _hubContext = hubContext;
        }

        public async Task<(bool Success, string MessageId)> SendMessage(string userId, string message, string from = "Admin")
        {
            try
            {
                var chatMessage = new ChatMessage
                {
                    UserId = userId,
                    Text = message,
                    Timestamp = DateTime.UtcNow.ToString("HH:mm:ss"),
                    From = from,
                    Status = "sent"
                };
                Console.WriteLine($"Sending message to {userId}: {message} from {from}");
                PushResponse response = await firebaseClient.PushAsync($"messages/{userId}", chatMessage);
                if (response.StatusCode == System.Net.HttpStatusCode.OK)
                {
                    string messageId = response.Result.name;
                    chatMessage.Id = messageId;
                    await firebaseClient.UpdateAsync($"messages/{userId}/{messageId}", chatMessage);
                    Console.WriteLine($"Message saved to Firebase with ID: {messageId}");
                    await _hubContext.Clients.All.SendAsync("ReceiveMessage", userId, message, from);
                    Console.WriteLine($"SignalR sent: ReceiveMessage to {userId}");
                    return (true, messageId);
                }
                Console.WriteLine("Failed to save message to Firebase");
                return (false, null);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
                return (false, null);
            }
        }

        public async Task<List<ChatMessage>> GetMessagesForUser(string userId)
        {
            try
            {
                FirebaseResponse response = await firebaseClient.GetAsync($"messages/{userId}");
                if (response.Body != "null")
                {
                    var messagesDict = response.ResultAs<Dictionary<string, ChatMessage>>();
                    var messages = messagesDict?.Select(m =>
                    {
                        m.Value.Id = m.Key;
                        return m.Value;
                    }).OrderBy(m => DateTime.ParseExact(m.Timestamp, "HH:mm:ss", null)).ToList() ?? new List<ChatMessage>();
                    Console.WriteLine($"Retrieved {messages.Count} messages for {userId}");
                    return messages;
                }
                return new List<ChatMessage>();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
                return new List<ChatMessage>();
            }
        }
        public async Task<List<string>> GetActiveUsers()
        {
            try
            {
                FirebaseResponse response = await firebaseClient.GetAsync("messages");
                if (response.Body != "null")
                {
                    var messagesDict = response.ResultAs<Dictionary<string, Dictionary<string, ChatMessage>>>();
                    var users = messagesDict?.Keys.ToList() ?? new List<string>();
                    Console.WriteLine($"Found {users.Count} active users");
                    return users;
                }
                return new List<string>();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
                return new List<string>();
            }
        }
    }
    public interface IChatService
    {
        Task<(bool Success, string MessageId)> SendMessage(string userId, string message, string from);
        Task<List<ChatMessage>> GetMessagesForUser(string userId);
        Task<List<string>> GetActiveUsers();
    }
}
