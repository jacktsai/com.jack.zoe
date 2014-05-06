package com.jack.zoe;

import android.app.Activity;
import android.content.res.Resources;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

class MessageAnimation {
	
	class MessagePair {
		public String Message;
		public TextView TextView;
	}
	
	final private Activity context;
	final private MessagePair[] messagePairs;
	final Timer timer = new Timer();
	
	public MessageAnimation(Activity context) {
		Resources resources = context.getResources();

        MessagePair greetings = new MessagePair();
        greetings.Message = resources.getString(R.string.greetings);
        greetings.TextView = (TextView)context.findViewById(R.id.greetings);

        MessagePair message1 = new MessagePair();
        message1.Message = resources.getString(R.string.message1);
        message1.TextView = (TextView)context.findViewById(R.id.message1);

        MessagePair message2 = new MessagePair();
        message2.Message = resources.getString(R.string.message2);
        message2.TextView = (TextView)context.findViewById(R.id.message2);

        MessagePair message3 = new MessagePair();
        message3.Message = resources.getString(R.string.message3);
        message3.TextView = (TextView)context.findViewById(R.id.message3);

        MessagePair message4 = new MessagePair();
        message4.Message = resources.getString(R.string.message4);
        message4.TextView = (TextView)context.findViewById(R.id.message4);

        MessagePair message5 = new MessagePair();
        message5.Message = resources.getString(R.string.message5);
        message5.TextView = (TextView)context.findViewById(R.id.message5);

        MessagePair signature = new MessagePair();
        signature.Message = resources.getString(R.string.signature);
        signature.TextView = (TextView)context.findViewById(R.id.signature);

        this.context = context;
		this.messagePairs = new MessagePair[] { greetings, message1, message2, message3, message4, message5, signature };
	}
	
	private void clearAllMessage() {
        for (MessagePair messagePair : this.messagePairs) {
            messagePair.TextView.setText("");
        }
	}
	
	public void start() {
		this.clearAllMessage();
		
		final TimerTask task = new TimerTask() {
			private MessageRunnable currentMessage;
			private int currentIndex = -1;
			
			@Override
			public void run() {
				if (this.isRunning()) {
					context.runOnUiThread(this.currentMessage);
				} else {
                    if (this.hasNext()) {
                        currentMessage = this.createNextRunner();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                    } else {
                        timer.cancel();
                    }
                }
			}
			
			private boolean isRunning() {
				return currentMessage != null && !currentMessage.getFinished();
			}
			
			private boolean hasNext() {
				if (currentMessage != null && currentMessage.getFinished() ) {
					if (currentIndex == messagePairs.length - 1) {
						return false;
					}
				}
				
				return true;
			}
			
			private MessageRunnable createNextRunner() {
				currentIndex++;
				
				String message = messagePairs[currentIndex].Message;
				TextView textView = messagePairs[currentIndex].TextView;
				
				return new MessageRunnable(message, textView);
			}
		};
		
		this.timer.schedule(task, 1000, 200);
	}
	
	public void cancel() {
		this.timer.cancel();
	}
	
	class MessageRunnable implements Runnable {
		final private char[] messageChars;
		final private TextView textView;
		
		private int displayedLength = 0;
		
		public MessageRunnable(String message, TextView textView) {
			this.messageChars = message.toCharArray();
			this.textView = textView;
		}
		
		public boolean getFinished() {
			return this.displayedLength == this.messageChars.length;
		}

		public void run() {
			this.displayedLength = this.displayedLength + 1;
			this.textView.setText(this.messageChars, 0, this.displayedLength);
		}		
	}
}
