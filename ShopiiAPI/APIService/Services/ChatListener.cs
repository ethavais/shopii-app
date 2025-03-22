using APIService.Models;
using FireSharp;
using FireSharp.Interfaces;
using Microsoft.AspNetCore.SignalR;
using Newtonsoft.Json;

namespace APIService.Services
{
    public class ChatListener : BackgroundService,IChatListener
    {
        private readonly IFirebaseClient _firebaseClient;
        private readonly IHubContext<ChatHub> _hubContext;
        private readonly ILogger<ChatListener> _logger;

        public ChatListener(IHubContext<ChatHub> hubContext, ILogger<ChatListener> logger)
        {
            _firebaseClient = FirebaseConfigSetup.GetClient();
            _hubContext = hubContext;
            _logger = logger;
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            _logger.LogInformation("Starting listener for new users");
            await ListenForNewUsers(stoppingToken);
        }

        public async Task ListenForNewUsers(CancellationToken stoppingToken)
        {
            await _firebaseClient.OnAsync("activeUsers", async (sender, args, context) =>
            {
                var activeUsers = await GetActiveUsers();
                _logger.LogInformation($"Found {activeUsers.Count} active users");

                foreach (var userId in activeUsers)
                {
                    await ListenForMessages(userId, stoppingToken);
                }
            });
        }

        public async Task<List<string>> GetActiveUsers()
        {
            var activeUsersSnapshot = await _firebaseClient.GetAsync("activeUsers");
            if (activeUsersSnapshot.Body == "null") return new List<string>();
            return JsonConvert.DeserializeObject<Dictionary<string, bool>>(activeUsersSnapshot.Body)
                ?.Keys.ToList() ?? new List<string>();
        }

        public async Task ListenForMessages(string userId, CancellationToken stoppingToken)
        {
            _logger.LogInformation($"Starting listener for {userId}");
            var messagesSnapshot = await _firebaseClient.GetAsync($"messages/{userId}");
            if (messagesSnapshot.Body != "null")
            {
                var messages = JsonConvert.DeserializeObject<Dictionary<string, ChatMessage>>(messagesSnapshot.Body);
                _logger.LogInformation($"Retrieved {messages?.Count ?? 0} messages for {userId}");
            }

            await _firebaseClient.OnAsync($"messages/{userId}", (sender, args, context) =>
            {
                var messageData = args.Data;
                if (string.IsNullOrEmpty(messageData) || messageData == "null") return;

                var messageDict = JsonConvert.DeserializeObject<Dictionary<string, ChatMessage>>(messageData);
                var latestMessage = messageDict?.Values.OrderByDescending(m => m.Timestamp).FirstOrDefault();
                if (latestMessage != null)
                {
                    _logger.LogInformation($"New message for {userId}: {latestMessage.Text}");
                    // Gửi thông báo qua SignalR
                    _hubContext.Clients.All.SendAsync("ReceiveMessage", userId, latestMessage.Text, latestMessage.From, stoppingToken);
                    return;
                }
                return;
            });
        }
    }
    public interface IChatListener
    {
        Task ListenForNewUsers(CancellationToken stoppingToken);
        Task<List<string>> GetActiveUsers();
        Task ListenForMessages(string userId, CancellationToken stoppingToken);

    }

}
