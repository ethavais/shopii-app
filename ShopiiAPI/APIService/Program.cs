using APIService.Data;
using APIService.Repositories;
using APIService.Services;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using static APIService.Services.PaymentService;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllers();
builder.Services.AddHttpContextAccessor();
builder.Services.AddSignalR().AddHubOptions<ChatHub>(options =>
{
    options.EnableDetailedErrors = true;
});


// Register DbContext
builder.Services.AddDbContext<ShopiiDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DB")));
//payos
builder.Services.AddScoped<Net.payOS.PayOS>(provider =>
{
    // Lấy các giá trị từ cấu hình (appsettings.json hoặc environment variables)
    string clientId = builder.Configuration["PAYOS_CLIENT:PAYOS_CLIENT_ID"] ?? throw new Exception("Cannot find PAYOS_CLIENT_ID in configuration");
    string apiKey = builder.Configuration["PAYOS_CLIENT:PAYOS_API_KEY"] ?? throw new Exception("Cannot find PAYOS_API_KEY in configuration");
    string checksumKey = builder.Configuration["PAYOS_CLIENT:PAYOS_CHECKSUM_KEY"] ?? throw new Exception("Cannot find PAYOS_CHECKSUM_KEY in configuration");

    // Trả về instance của PayOS
    return new Net.payOS.PayOS(clientId, apiKey, checksumKey);
});
//payment
builder.Services.AddScoped<IPaymentService, PaymentService>();
builder.Services.AddScoped<IPaymentRepository, PaymentRepository>();
//chat
builder.Services.AddSingleton<IChatListener, ChatListener>();
builder.Services.AddSingleton<IChatService, ChatService>();
// Register Repositories
builder.Services.AddScoped(typeof(IGenericRepository<>), typeof(GenericRepository<>));
builder.Services.AddScoped<IProductRepository, ProductRepository>();

// Register Services
builder.Services.AddScoped<IProductService, ProductService>();

// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

//Config firebase


// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();
app.MapHub<ChatHub>("/chatHub");
//var listener = app.Services.GetRequiredService<IChatListener>();
//listener.ListenToNewUsers((userId) =>
//{
//    Console.WriteLine($"New user chatting: {userId}");
//    // Tự động lắng nghe tin nhắn của người dùng mới
//    listener.ListenToUserMessages(userId, (message) =>
//    {
//        Console.WriteLine($"New message from {message.From}: {message.Text}");
//    });
//});
app.Run();
