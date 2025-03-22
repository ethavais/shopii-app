using Microsoft.AspNetCore.SignalR;

namespace APIService.Services
{
    public class ChatHub : Hub
    {
        private readonly IChatService _chatService;
        private readonly IChatListener _chatListener;

        public ChatHub(IChatService chatService, IChatListener chatListener)
        {
            _chatService = chatService;
            _chatListener = chatListener ?? throw new ArgumentNullException(nameof(chatListener));
        }
        public override async Task OnConnectedAsync()
        {
            // Bắt đầu lắng nghe tin nhắn từ Firebase khi Hub được khởi tạo
            Console.WriteLine("Client connected to ChatHub");
            //_chatListener.ListenToNewUsers(userId =>
            //{
            //    //_chatListener.ListenToUserMessages(userId, async msg =>
            //    //{
            //    //    // Gửi tin nhắn từ user tới admin qua SignalR
            //    //    await Clients.All.SendAsync("ReceiveMessage", userId, msg.Text, "user");
            //    //});
            //});

            await base.OnConnectedAsync();
        }
        public async Task SendMessage(string userId, string message)
        {
            var (success, messageId) = await _chatService.SendMessage(userId, message, "admin");
        }
    }
}
